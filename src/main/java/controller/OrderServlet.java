package controller;

import model.Order;
import model.User;
import model.enums.Role;
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

	private OrderRepository repository;
	private OrderService orderService = new OrderService(repository);


	@Override
	public void init() throws ServletException {
		this.repository = new OrderRepositoryImpl();
		this.orderService = new OrderService(repository);
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());

		HttpSession session = request.getSession();

		User user = new User();
		user.setFirstName("client");
		user.setEmail("client");
		user.setRole(Role.CLIENT);

		session.setAttribute("user", user);

		User loggedInUser = (User) session.getAttribute("user");

		ServletContext servletContext = request.getServletContext();
		WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		context.setVariable("user", loggedInUser);

		int page = Integer.parseInt(request.getParameter("page") != null ? request.getParameter("page") : "0");
		int size = 5;

		List<Order> orderList_No_historique = orderService.getAllOrders_No_historique(page, size);
		context.setVariable("orderList_No_historique", orderList_No_historique);

		response.setContentType("text/html;charset=UTF-8");
		templateEngine.process("views/dashboard/order", context, response.getWriter());
	}



	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}
