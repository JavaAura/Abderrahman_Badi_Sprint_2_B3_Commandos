package controller;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import model.Admin;
import model.Product;
import model.User;
import model.enums.Role;
import repository.implementation.ProductRepositoryImpl;
import repository.interfaces.ProductRepository;
import service.ProductService;
import util.ThymeleafUtil;

import java.io.IOException;
import java.util.List;

public class ProductServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(ProductServlet.class);
	private static final int PAGE_SIZE = 4;

	private final ProductRepository productRepository = new ProductRepositoryImpl();
	private final ProductService productService = new ProductService(productRepository);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());
		ServletContext servletContext = request.getServletContext();
		WebContext context = new WebContext(request, response, servletContext, request.getLocale());

		HttpSession session = request.getSession();

		User loggedInUser = (User) session.getAttribute("user");

		if (loggedInUser == null) {
			response.sendRedirect("/Commandos");
			return;
		}

		int page = 1;
		String pageParam = request.getParameter("page");
		if (pageParam != null) {
			try {
				page = Integer.parseInt(pageParam);
			} catch (NumberFormatException e) {
				logger.error("Error parsing pageParam: {}", pageParam, e);
			}
		}

		List<Product> products = productService.getAllProducts(page, PAGE_SIZE);
		long totalProductCount = productService.getTotalProductCount();
		int totalPages = (int) Math.ceil((double) totalProductCount / PAGE_SIZE);

		context.setVariable("user", loggedInUser);
		context.setVariable("products", products);
		context.setVariable("totalPages", totalPages);
		context.setVariable("pageNumber", page);

		response.setContentType("text/html;charset=UTF-8");

		templateEngine.process("views/dashboard/products", context, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");

		if ("add".equals(action)) {
			addProduct(request, response);
		} else if ("update".equals(action)) {
			updateProduct(request, response);
		} else if ("delete".equals(action)) {
			deleteProduct(request, response);
		} else if ("search".equals(action)) {
			searchProduct(request, response);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action.");
		}

	}

	private void searchProduct(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("name");
		List<Product> products;

		if (name != null && !name.trim().isEmpty()) {

			products = productService.searchProduct(name);
		} else {

			products = productService.getAllProducts(1, PAGE_SIZE);
		}

		request.setAttribute("products", products);
		request.setAttribute("pageNumber", 1);
		request.setAttribute("totalPages", 1);

		TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());
		ServletContext servletContext = request.getServletContext();
		WebContext context = new WebContext(request, response, servletContext, request.getLocale());

		context.setVariable("products", products);
		context.setVariable("pageNumber", 1);
		context.setVariable("totalPages", 1);
		context.setVariable("user", request.getSession().getAttribute("user"));

		response.setContentType("text/html;charset=UTF-8");
		templateEngine.process("views/dashboard/products", context, response.getWriter());
	}

	private void updateProduct(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// long userId = Long.parseLong(request.getParameter("user_id"));
		// try {
		// 	User user = userService.getUser(userId).orElse(null);
		// 	if (user != null) {
		// 		String jsonResponse = convertUserToJson(user);

		// 		response.setStatus(HttpServletResponse.SC_OK);
		// 		response.setContentType("application/json");
		// 		response.getWriter().write(jsonResponse);
		// 	} else {
		// 		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		// 		response.getWriter().write("{\"error\": \"User not found\"}");
		// 	}

		// } catch (Exception e) {
		// 	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		// 	response.setContentType("application/json");
		// 	response.getWriter().write("{\"error\": \"error\"}");
		// }

	}

	private void addProduct(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		double price = Double.parseDouble(request.getParameter("price"));
		boolean isDeleted = Boolean.parseBoolean(request.getParameter("isdeleted"));
		int stock = Integer.parseInt(request.getParameter("stock"));

		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setIsDeleted(isDeleted);
		product.setPrice(price);
		product.setStock(stock);

		productService.addProduct(product);
		response.sendRedirect("/Commandos/dashboard/products?action=list&page=1");
	}

	private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Long id = Long.parseLong(request.getParameter("id"));

		productService.deleteProduct(id);

		// Redirect to the products page after successful deletion
		response.sendRedirect(request.getContextPath() + "/dashboard/products");
	}

}
