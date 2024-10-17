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
import model.User;
import model.enums.Role;
import repository.implementation.UserRepositoryImpl;
import repository.interfaces.UserRepository;
import service.UserService;
import util.ThymeleafUtil;

import java.io.IOException;
import java.util.List;

public class UserServlet extends HttpServlet {
	private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);

	private UserRepository userRepository = new UserRepositoryImpl();
	private UserService userService = new UserService(userRepository);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());

		ServletContext servletContext = request.getServletContext();

		WebContext context = new WebContext(request, response, servletContext, request.getLocale());

		HttpSession session = request.getSession();

		Admin user = new Admin();
		user.setFirstName("admin");
		user.setEmail("admin@youcode.ma");
		user.setRole(Role.ADMIN);
		user.setLevelAccess(1);

		session.setAttribute("user", user);

		User loggedInUser = (User) session.getAttribute("user");

		if (loggedInUser.getRole() == Role.CLIENT)
			templateEngine.process("views/index", context, response.getWriter());

		int page = 1; // default page is 1
		String pageParam = request.getParameter("page");

		if (pageParam != null) {
			try {
				page = Integer.parseInt(pageParam);
			} catch (NumberFormatException e) {
				logger.error("Error parsing pageParam", e);
				templateEngine.process("views/dashboard/users", context, response.getWriter());
			}
		}

		List<User> users = userService.getAllUsers(page, user.getLevelAccess());
		int totalPages = userService.getTotalPageNumber(user.getLevelAccess());

		context.setVariable("user", loggedInUser);
		context.setVariable("users", users);

		// Set content type for the response
		response.setContentType("text/html;charset=UTF-8");

		templateEngine.process("views/dashboard/users", context, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

}
