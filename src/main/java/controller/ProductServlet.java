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

import model.Product;
import model.User;
import repository.implementation.ProductRepositoryImpl;
import repository.interfaces.ProductRepository;
import service.ProductService;
import util.ThymeleafUtil;
import util.Validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(ProductServlet.class);
	private static final int PAGE_SIZE = 5;

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

		String message = (String) session.getAttribute("message");
		@SuppressWarnings("unchecked")
		List<String> errorMessages = (List<String>) session.getAttribute("errorMessages");

		logger.info("error messages from session : " + errorMessages);

		if (message != null) {
			context.setVariable("message", message);
			session.removeAttribute("message"); // Clear after displaying
		}
		if (errorMessages != null) {
			context.setVariable("errorMessages", errorMessages);
			session.removeAttribute("errorMessages");
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
		if (action != null) {
			logger.info("action is : " + action);
			switch (action) {
				case "add":
					addProduct(request, response);
					break;
				case "update":
					logger.info("action is here");
					updateProduct(request, response);
					break;
				case "delete":
					deleteProduct(request, response);
					break;
				case "search":
					searchProduct(request, response);
					break;
				case "get":
					try {
						long productId = Long.parseLong(request.getParameter("product_id"));
						Product product = productService.getProduct(productId).orElse(null);
						if (product != null) {
							String jsonResponse = convertProductToJson(product);
							response.setStatus(HttpServletResponse.SC_OK);

							response.setContentType("application/json");
							response.getWriter().write(jsonResponse);
						} else {
							response.setStatus(HttpServletResponse.SC_NOT_FOUND);
							response.getWriter().write("{\"error\": \"Product not found\"}");
						}

					} catch (Exception e) {
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						response.setContentType("application/json");
						response.getWriter().write("{\"error\": \"error\"}");
					}
					return;
				default:
					response.sendRedirect("products");
					break;
			}
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
		List<String> errors = new ArrayList<>();
		HttpSession session = request.getSession();
		int stock;
		double price;

		Long id = Long.parseLong(request.getParameter("productId"));
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		try {
			stock = Integer.parseInt(request.getParameter("stock"));
		} catch (Exception e) {
			errors.add("Stock should be a number");
			session.setAttribute("errorMessages", errors);
			response.sendRedirect("products");
			return;
		}
		try {
			price = Double.parseDouble(request.getParameter("price"));
		} catch (Exception e) {
			errors.add("Stock should be a number");
			session.setAttribute("errorMessages", errors);
			response.sendRedirect("products");
			return;
		}

		Product product = productService.getProduct(id).orElse(null);

		if (product == null) {
			errors.add("Couldn't find product");
			session.setAttribute("errorMessages", errors);
			response.sendRedirect("products");
			return;
		}

		product.setName(name);
		product.setDescription(description);
		product.setPrice(price);
		product.setStock(stock);

		errors.addAll(Validator.validateProduct(product));

		if (errors.isEmpty()) {
			productService.updateProduct(product);
			session.setAttribute("message", "User updated successfully!");
		} else {
			logger.info("errors : " + errors);
			session.setAttribute("errorMessages", errors);
		}

		response.sendRedirect("products");

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
		response.sendRedirect("products");
	}

	private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Long id = Long.parseLong(request.getParameter("id"));

		productService.deleteProduct(id);

		response.sendRedirect("products");
	}

	private String convertProductToJson(Product product) {
		StringBuilder json = new StringBuilder("{");
		json.append("\"id\":").append(product.getId()).append(",");
		json.append("\"name\":\"").append(product.getName()).append("\",");
		json.append("\"description\":\"").append(product.getDescription()).append("\",");
		json.append("\"price\":").append(product.getPrice()).append(",");
		json.append("\"stock\":").append(product.getStock());

		json.append("}");
		return json.toString();
	}
}
