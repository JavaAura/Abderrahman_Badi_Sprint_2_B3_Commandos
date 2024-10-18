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
import java.util.Optional;

public class ProductServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(ProductServlet.class);
	 private static final int PAGE_SIZE = 10;

	private final ProductRepository productRepository = new ProductRepositoryImpl();
	private final ProductService productService = new ProductService(productRepository);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());
	    ServletContext servletContext = request.getServletContext();
	    WebContext context = new WebContext(request, response, servletContext, request.getLocale());

	    HttpSession session = request.getSession();
	    
	    // Hardcoded admin user for testing
	    Admin user = new Admin();
	    user.setFirstName("admin");
	    user.setEmail("admin@youcode.ma");
	    user.setRole(Role.ADMIN);
	    user.setLevelAccess(1);
	    
	    // Set the test user in the session
	    session.setAttribute("user", user);
	    
	    // Retrieve the logged-in user from the session
	    User loggedInUser = (User) session.getAttribute("user");

	    // Ensure user session exists and has the correct role
	    if (loggedInUser == null || loggedInUser.getRole() != Role.ADMIN) {
	        logger.warn("Unauthorized access attempt by user: {}", (loggedInUser != null ? loggedInUser.getEmail() : "unknown"));
	        response.sendRedirect("/login");  // Redirect if not logged in or not an admin
	        return;
	    }

	    // Pagination logic
	    int page = 1; // Default page is 1
	    String pageParam = request.getParameter("page");
	    if (pageParam != null) {
	        try {
	            page = Integer.parseInt(pageParam);
	        } catch (NumberFormatException e) {
	            logger.error("Error parsing pageParam: {}", pageParam, e);
	        }
	    }

	    // Fetching products and total count
	    List<Product> products = productService.getAllProducts(page, PAGE_SIZE); // Use PAGE_SIZE here
	    long totalProductCount = productService.getTotalProductCount(); // Total count of products
	    int totalPages = (int) Math.ceil((double) totalProductCount / PAGE_SIZE); // Calculate total pages

	    // Add variables to Thymeleaf context
	    context.setVariable("user", loggedInUser);
	    context.setVariable("products", products);
	    context.setVariable("totalPages", totalPages); // Correctly calculated total pages
	    context.setVariable("pageNumber", page);

	    // Set content type for the response
	    response.setContentType("text/html;charset=UTF-8");

	    // Render the Thymeleaf template for products page
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
	        // Search products by name
	        products = productService.searchProduct(name);
	    } else {
	        // Fetch all products if no search query is provided
	        products = productService.getAllProducts(1, PAGE_SIZE);
	    }

	    // Prepare the response
	    request.setAttribute("products", products);
	    request.setAttribute("pageNumber", 1); // Reset page number for search results
	    request.setAttribute("totalPages", 1); // Adjust accordingly if you implement pagination for search results

	    TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());
	    ServletContext servletContext = request.getServletContext();
	    WebContext context = new WebContext(request, response, servletContext, request.getLocale());

	    context.setVariable("products", products);
	    context.setVariable("pageNumber", 1); // Update this if implementing pagination for search results
	    context.setVariable("totalPages", 1); // Adjust if necessary
	    context.setVariable("user", request.getSession().getAttribute("user"));

	    // Render the Thymeleaf template for products page
	    response.setContentType("text/html;charset=UTF-8");
	    templateEngine.process("views/dashboard/products", context, response.getWriter());
	}


	private void updateProduct(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Long id = Long.parseLong(request.getParameter("id"));
		
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		double price = Double.parseDouble(request.getParameter("price"));
		boolean isDeleted = Boolean.parseBoolean(request.getParameter("isdeleted"));
		int stock = Integer.parseInt(request.getParameter("stock"));
		
		Optional<Product> products = productService.getProduct(id);
		
		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setIsDeleted(isDeleted);
		product.setPrice(price);
		product.setStock(stock);
		
		productService.updateProduct(product);
		response.sendRedirect("/products?action=list&page=1");

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
		response.sendRedirect("/products?action=list&page=1");
	}

	private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Long id = Long.parseLong(request.getParameter("id"));
		productService.deleteProduct(id);
		response.sendRedirect("/products?action=list&page=1");
		
		
	}
}
