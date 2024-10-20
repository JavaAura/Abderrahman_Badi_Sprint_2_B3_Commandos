package controller;

import model.Client;
import model.Order;
import model.Product;
import model.User;
import model.enums.Statut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import repository.implementation.OrderRepositoryImpl;
import repository.implementation.ProductRepositoryImpl;
import repository.interfaces.OrderRepository;
import repository.interfaces.ProductRepository;
import service.OrderService;
import service.ProductService;
import util.ThymeleafUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(OrderServlet.class);

	private OrderRepository repository;
	private ProductRepository productRepository;
	private OrderService orderService;
	private ProductService productService;

	@Override
	public void init() throws ServletException {
		this.repository = new OrderRepositoryImpl();
		this.orderService = new OrderService(repository);
		this.productRepository = new ProductRepositoryImpl();
		this.productService = new ProductService(productRepository);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User loggedInUser = (User) session.getAttribute("user");

		logger.info("Session user: " + loggedInUser);

		if (loggedInUser == null) {
			response.sendRedirect("/Commandos");
			return;
		}

		switch (loggedInUser.getRole()) {
			case ADMIN:
				showAllOrders(request, response, loggedInUser);
				break;

			case CLIENT:
				String orderIdParam = request.getParameter("order_id");
				if (orderIdParam != null && !orderIdParam.trim().isEmpty()) {
					long orderId = Long.parseLong(orderIdParam);
					logger.info("Order id is here : " + orderId);
					showOrderDetails(request, response, orderId, loggedInUser);
				} else {
					showCLientOrders(request, response, loggedInUser);
				}
				break;

			default:
				response.sendRedirect("/Commandos");
				return;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User loggedInUser = (User) session.getAttribute("user");

		if (loggedInUser == null) {
			response.sendRedirect("/Commandos");
			return;
		}

		String action = request.getParameter("action");




		if (action != null) {
			switch (action) {
				case "add":
					try {
						String[] selectedProductIds = request.getParameterValues("selectedProducts");
						List<Product> selectedProducts = new ArrayList<>();

						if (selectedProductIds != null) {
							for (String productId : selectedProductIds) {
								Optional<Product> optionalProduct = productService
										.getProduct(Long.parseLong(productId));
								if (optionalProduct.isPresent()) {
									selectedProducts.add(optionalProduct.get());
								} else {

									logger.warn("Produit avec l'ID {} non trouvé", productId);
								}
							}
						}

						Client client = (Client) loggedInUser;
						addOrderWithProducts(selectedProducts, client);

						String Refererche = request.getHeader("Referer");
						response.sendRedirect(Refererche);

						response.setStatus(HttpServletResponse.SC_OK);
						response.setContentType("application/json");
						response.getWriter().write("{\"message\": \"Order added successfully !\"}");
					} catch (Exception e) {
						logger.error("Erreur lors de l'ajout de la commande : {}", e.getMessage());
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						response.setContentType("application/json");
						response.getWriter().write("{\"error\": \"Erreur lors de l'ajout de la commande\"}");
					}

				case "delete":
					Long order_Id = Long.parseLong(request.getParameter("id"));

					try {

						orderService.deleteOrder(order_Id);
						logger.info("Order with ID " + order_Id + " has been deleted.");

						String referer = request.getHeader("Referer");
						response.sendRedirect(referer);
						return;
					} catch (Exception e) {
						logger.error("Error deleting order", e);
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting order");
					}
					break;
				case "update":

					try {
						Long orderId = Long.parseLong(request.getParameter("id"));
						Order orderToUpdate = orderService.getOrderById(orderId);

						if (orderToUpdate == null) {
							response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
							return;
						}

					    System.out.println("upadet buuuuuuuuuuuuuuuuuuuuuuuuuuuussssssssssssssssssssssssssssssssssssssssssss");
						String newStatus = request.getParameter("status");
						orderToUpdate.setOrderStatut(Statut.valueOf(newStatus));


						String[] selectedProductIds = request.getParameterValues("selectedProducts");
						List<Product> selectedProducts = new ArrayList<>();

						if (selectedProductIds != null) {
							for (String productId : selectedProductIds) {
								Optional<Product> optionalProduct = productService.getProduct(Long.parseLong(productId));
								if (optionalProduct.isPresent()) {
									selectedProducts.add(optionalProduct.get());
								} else {
									logger.warn("Product with ID {} not found", productId);
								}
							}
						}

						orderToUpdate.setProducts(selectedProducts);

						Order updatedOrder = orderService.updateOrder(orderToUpdate, loggedInUser);
						logger.info("Order with ID " + updatedOrder.getId() + " has been updated.");

						response.setStatus(HttpServletResponse.SC_OK);
						response.setContentType("application/json");
						response.getWriter().write("{\"message\": \"Order updated successfully!\"}");

						String referer = request.getHeader("Referer");
						response.sendRedirect(referer);
					} catch (IllegalArgumentException e) {
						logger.error("Invalid status value: {}", e.getMessage());
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid status value");
					} catch (Exception e) {
						logger.error("Error updating order: {}", e.getMessage());
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						response.setContentType("application/json");
						response.getWriter().write("{\"error\": \"Error updating order\"}");
					}
					break;


				case "update_status":
					try {
						long orderId = Long.parseLong(request.getParameter("order_id"));
						Statut orderStatus = Statut.valueOf(request.getParameter("status"));
						Order order = orderService.getOrderById(orderId);

						if (order != null) {
							order.setOrderStatut(orderStatus);

							orderService.updateOrder(order, loggedInUser);

							response.setStatus(HttpServletResponse.SC_OK);
							response.setContentType("application/json");
							response.getWriter().write("{\"message\": \"Status updated successfully !\"}");
						} else {
							response.setStatus(HttpServletResponse.SC_NOT_FOUND);
							response.getWriter().write("{\"error\": \"User not found\"}");
						}

					} catch (Exception e) {
						logger.info("Error occured during order status update .", e);
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						response.setContentType("application/json");
						response.getWriter().write("{\"error\": \"unkown error occured\"}");
					}
					return;
				default:
					break;
			}
		}
		response.sendRedirect("order");
	}

	protected void showCLientOrders(HttpServletRequest request, HttpServletResponse response, User loggedInUser)
			throws ServletException, IOException {
		TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());

		ServletContext servletContext = request.getServletContext();
		WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		context.setVariable("user", loggedInUser);

		// Pagination setup
		int page = 1;
		String pageParam = request.getParameter("page");
		if (pageParam != null && !pageParam.isEmpty()) {
			try {
				page = Integer.parseInt(pageParam);
			} catch (NumberFormatException e) {
				page = 1;
			}
		}

		int size = 5;

		List<Order> orderList_No_historique = orderService.getAllOrders_No_historique(loggedInUser.getId(), page, size);
		List<Product> products = productService.getAllProducts();

		int totalOrders = orderService.getTotalOrderCountByStatus(loggedInUser);
		int totalPages = (int) Math.ceil((double) totalOrders / size);

		context.setVariable("orderList_No_historique", orderList_No_historique);
		context.setVariable("pageNumber", page);
		context.setVariable("totalPages", totalPages);
		context.setVariable("listofProduct", products);

		response.setContentType("text/html;charset=UTF-8");
		templateEngine.process("views/dashboard/order", context, response.getWriter());
	}

	protected void showAllOrders(HttpServletRequest request, HttpServletResponse response, User loggedInUser)
			throws ServletException, IOException {
		TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());

		ServletContext servletContext = request.getServletContext();
		WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		context.setVariable("user", loggedInUser);

		int page = 1;
		String pageParam = request.getParameter("page");
		if (pageParam != null && !pageParam.isEmpty()) {
			try {
				page = Integer.parseInt(pageParam);
			} catch (NumberFormatException e) {
				page = 1;
			}
		}

		int size = 5;

		List<Order> orders = orderService.getAllOrders(page, size);
		logger.info("Orders retrieved: " + orders);

		int totalOrders = orderService.getTotalOrderCount();
		int totalPages = (int) Math.ceil((double) totalOrders / size);
		logger.info("Orders count : " + totalOrders);

		context.setVariable("orders", orders);
		context.setVariable("pageNumber", page);
		context.setVariable("totalPages", totalPages);

		response.setContentType("text/html;charset=UTF-8");
		templateEngine.process("views/dashboard/admin/order", context, response.getWriter());
	}

	public void addOrderWithProducts(List<Product> products, Client client) {

		Order order = new Order();
		order.setOrderDate(LocalDate.now());
		order.setOrderStatut(Statut.WAITING);
		order.setClient(client);

		order.setProducts(products);

		orderService.createOrder(order);
	}

	protected void showOrderDetails(HttpServletRequest request, HttpServletResponse response, long orderId,
			User loggedInUser)
			throws ServletException, IOException {
		TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());
		ServletContext servletContext = request.getServletContext();
		WebContext context = new WebContext(request, response, servletContext, request.getLocale());

		try {
			List<Product> ListProducts = productService.getAllProducts();
			Order order = orderService.getOrderById(orderId);
			List<Product> mesProduct = productService.getMesProducts(orderId);
			System.out.println("kolchi !!! product  : " + ListProducts);
			System.out.println("mes product  : " + mesProduct);
			logger.info("order in order details are : " + order);

			if (order != null) {
				if (order.getClient().getId() != loggedInUser.getId()){
					logger.warn("Order belongs to client with id : " + order.getClient().getId() + " and requested from client with id : " + loggedInUser.getId());
					response.sendRedirect("/Commandos");
					return;
				}

				context.setVariable("user", loggedInUser);
				context.setVariable("order", order);
				context.setVariable("ListProducts",ListProducts );
				context.setVariable("mesProduct",mesProduct);
				response.setContentType("text/html;charset=UTF-8");
				templateEngine.process("views/dashboard/order_details", context, response.getWriter());
			} else {
				logger.warn("Order with ID {} not found", orderId);
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
				response.sendRedirect("order");
				return;
			}
		} catch (Exception e) {
			logger.error("Error retrieving order details for ID " + orderId, e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving order details");
		}
	}

}
