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
				String orderIdParam = request.getParameter("id");
				if (orderIdParam != null && !orderIdParam.trim().isEmpty()) {
					long orderId = Long.parseLong(orderIdParam);
					showOrderDetails(request, response, orderId);
				}
				showCLientOrders(request, response, loggedInUser);
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
								Optional<Product> optionalProduct = productService.getProduct(Long.parseLong(productId));
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

		logger.info("Orders retrieved: " + orderList_No_historique);

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





	protected void showOrderDetails(HttpServletRequest request, HttpServletResponse response, long orderId)
			throws ServletException, IOException {
		TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());
		ServletContext servletContext = request.getServletContext();
		WebContext context = new WebContext(request, response, servletContext, request.getLocale());

		try {

			Order order = orderService.getOrderById(orderId);

			if (order != null) {
				context.setVariable("order", order);
				response.setContentType("text/html;charset=UTF-8");
				templateEngine.process("views/dashboard/orderDetails", context, response.getWriter());
			} else {
				logger.warn("Order with ID {} not found", orderId);
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
			}
		} catch (Exception e) {
			logger.error("Error retrieving order details for ID " + orderId, e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving order details");
		}
	}



}
