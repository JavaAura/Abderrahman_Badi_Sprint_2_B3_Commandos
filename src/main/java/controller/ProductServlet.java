package controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Product;
import repository.implementation.ProductRepositoryImpl;
import repository.interfaces.ProductRepository;
import service.ProductService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ProductServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(ProductServlet.class);

	private final ProductRepository productRepository = new ProductRepositoryImpl();
	private final ProductService productService = new ProductService(productRepository);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");

		if ("list".equals(action)) {
			getAllProducts(request, response);
		} else if ("get".equals(action)) {
			getProduct(request, response);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action.");
		}

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


	private void getProduct(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Long id = Long.parseLong(request.getParameter("id"));
		Optional<Product> product = productService.getProduct(id);
		
		if(product.isPresent()) {
			request.setAttribute("product", product);
			request.getRequestDispatcher("/productDetails.html").forward(request, response);
		}else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
		}

	}

	private void getAllProducts(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		int pageNumber = Integer.parseInt(request.getParameter("id"));
		List<Product> products = productService.getAllProducts(pageNumber);
		request.setAttribute("products", products);
		request.getRequestDispatcher("/products.html").forward(request, response);

	}
	
	private void searchProduct(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("name");
		List<Product> products = productService.searchProduct(name);
		request.setAttribute("products", products);
		request.getRequestDispatcher("/products.html").forward(request, response);

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
		response.sendRedirect("products?action=list&page=1");

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
		response.sendRedirect("products?action=list&page=1");
	}

	private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Long id = Long.parseLong(request.getParameter("id"));
		productService.deleteProduct(id);
		response.sendRedirect("products?action=list&page=1");
		
		
	}
}
