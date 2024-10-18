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

import at.favre.lib.crypto.bcrypt.BCrypt;
import model.Admin;
import model.Client;
import model.User;
import model.enums.Role;
import repository.implementation.UserRepositoryImpl;
import repository.interfaces.UserRepository;
import service.UserService;
import util.ThymeleafUtil;
import util.Validator;

import java.io.IOException;
import java.util.ArrayList;
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

		String message = (String) session.getAttribute("message");
		List<String> errorMessages = (List<String>) session.getAttribute("errorMessages");

		if (message != null) {
			context.setVariable("message", message);
			session.removeAttribute("message");  // Clear after displaying
		}
		if (errorMessages != null) {
			context.setVariable("errorMessages", errorMessages);
			session.removeAttribute("errorMessages"); 
		}

		Admin loggedUser = new Admin();
		loggedUser.setFirstName("admin");
		loggedUser.setEmail("admin@youcode.ma");
		loggedUser.setRole(Role.ADMIN);
		loggedUser.setLevelAccess(1);	

		session.setAttribute("user", loggedUser);

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

		List<User> users = userService.getAllUsers(page, loggedUser.getLevelAccess());
		int totalPages = userService.getTotalPageNumber(loggedUser.getLevelAccess());

		context.setVariable("user", loggedInUser);
		context.setVariable("users", users);
		context.setVariable("totalPages", totalPages);
		context.setVariable("pageNumber", page);

		// Set content type for the response
		response.setContentType("text/html;charset=UTF-8");

		templateEngine.process("views/dashboard/users", context, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");

		if (action != null) {
			switch (action) {
				case "add":
					addUser(request, response);
					break;
				case "delete":
					deleteUser(request, response);
					break;
				case "update":
					updateUser(request, response);
					break;

				default:
					break;
			}
		}
		response.sendRedirect("users");
	}

	protected void addUser(HttpServletRequest request, HttpServletResponse response) {
		List<String> errors = new ArrayList<>();
		HttpSession session = request.getSession();

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		Role role = Role.valueOf(request.getParameter("role"));

		switch (role.toString()) {
			case "ADMIN":
				Admin admin = new Admin(firstName, lastName, email, password, role);
				logger.info("Admin to insert : " + admin);

				errors = Validator.validateUser(admin);
				logger.info("Validator errors : " + errors);

				if (errors.isEmpty()) {
					admin.setPassword(BCrypt.withDefaults().hashToString(12, admin.getPassword().toCharArray()));
					userService.addUser(admin);
					session.setAttribute("message", "Author added successfully!");
				} else {
					session.setAttribute("errorMessages", errors);
				}
				break;

			case "CLIENT":
				String addressDelivery = request.getParameter("addressDelivery");
				String paymentMethod = request.getParameter("paymentMethod");

				Client client = new Client(firstName, lastName, email, password, role, addressDelivery, paymentMethod);
				logger.info("Admin to insert : " + client);

				errors = Validator.validateUser(client);
				logger.info("Validator errors : " + errors);

				if (errors.isEmpty()) {
					client.setPassword(BCrypt.withDefaults().hashToString(12, client.getPassword().toCharArray()));
					userService.addUser(client);
					session.setAttribute("message", "Author added successfully!");
				} else {
					session.setAttribute("errorMessages", errors);
				}
				break;

			default:
				break;
		}

	}

	protected void updateUser(HttpServletRequest request, HttpServletResponse response) {

	}

	protected void deleteUser(HttpServletRequest request, HttpServletResponse response) {
		String authorIdString = request.getParameter("id");
		long userId = Integer.parseInt(authorIdString);

		userService.deleteUser(userId);
	}
}
