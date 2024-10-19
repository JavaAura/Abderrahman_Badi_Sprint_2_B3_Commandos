package controller;

import model.Order;
import model.Product;
import model.User;
import model.enums.Role;
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
import java.util.List;

public class OrderServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(OrderServlet.class);


	private OrderRepository repository;
	private OrderService orderService;
	private ProductService productService;
	private ProductRepository productRepository;

	@Override
	public void init() throws ServletException {
		this.repository = new OrderRepositoryImpl();
		this.productRepository = new ProductRepositoryImpl();
		this.orderService = new OrderService(repository);
		this.productService = new ProductService(productRepository);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());

		HttpSession session = request.getSession();
		User loggedInUser = (User) session.getAttribute("user");

		logger.info("Session user: " + loggedInUser);

		if (loggedInUser == null) {

			loggedInUser = new User();
			loggedInUser.setId(3L);
			loggedInUser.setFirstName("client");
			loggedInUser.setEmail("client3@youcode.ma");
			loggedInUser.setRole(Role.CLIENT);
			session.setAttribute("user", loggedInUser);
		}

		if (loggedInUser.getId() == null) {
			logger.error("User ID is null!");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID cannot be null.");
			return;
		}

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


		List<Product> listofProduct = productService.getAllProducts();

		logger.info("list product : " +listofProduct);

		int totalOrders = orderService.getTotalOrderCountByStatus();
		int totalPages = (int) Math.ceil((double) totalOrders / size);

        context.setVariable("listofProduct", listofProduct);
		context.setVariable("orderList_No_historique", orderList_No_historique);
		context.setVariable("pageNumber", page);
		context.setVariable("totalPages", totalPages);

		response.setContentType("text/html;charset=UTF-8");
		templateEngine.process("views/dashboard/order", context, response.getWriter());
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}
