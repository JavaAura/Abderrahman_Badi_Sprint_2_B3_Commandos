package controller;

import model.Order;
import model.User;
import model.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import repository.implementation.OrderRepositoryImpl;
import repository.interfaces.OrderRepository;
import service.OrderService;
import util.ThymeleafUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class OrderServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(OrderServlet.class);

	private OrderRepository repository;
	private OrderService orderService;

	@Override
	public void init() throws ServletException {
		this.repository = new OrderRepositoryImpl();
		this.orderService = new OrderService(repository);
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
		String action = request.getParameter("action");
		if (action != null) {
			switch (action) {
				case "add":
					break;
				case "delete":
					break;
				case "update":
					break;
				case "update_status":

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
		logger.info("Orders retrieved: " + orderList_No_historique);

		int totalOrders = orderService.getTotalOrderCountByStatus(loggedInUser);
		int totalPages = (int) Math.ceil((double) totalOrders / size);

		context.setVariable("orderList_No_historique", orderList_No_historique);
		context.setVariable("pageNumber", page);
		context.setVariable("totalPages", totalPages);

		response.setContentType("text/html;charset=UTF-8");
		templateEngine.process("views/dashboard/order", context, response.getWriter());
	}

	protected void showAllOrders(HttpServletRequest request, HttpServletResponse response, User loggedInUser)
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
		logger.info("Orders retrieved: " + orderList_No_historique);

		int totalOrders = orderService.getTotalOrderCountByStatus(loggedInUser);
		int totalPages = (int) Math.ceil((double) totalOrders / size);

		context.setVariable("orderList_No_historique", orderList_No_historique);
		context.setVariable("pageNumber", page);
		context.setVariable("totalPages", totalPages);

		response.setContentType("text/html;charset=UTF-8");
		templateEngine.process("views/dashboard/admin/order", context, response.getWriter());
	}
}
