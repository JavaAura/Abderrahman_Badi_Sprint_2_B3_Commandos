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

		User loggedInUser = (User) session.getAttribute("user");

		if (loggedInUser == null) {
			response.sendRedirect("/Commandos");
			return;
		}

		String message = (String) session.getAttribute("message");
		@SuppressWarnings("unchecked")
		List<String> errorMessages = (List<String>) session.getAttribute("errorMessages");

		if (message != null) {
			context.setVariable("message", message);
			session.removeAttribute("message"); // Clear after displaying
		}
		if (errorMessages != null) {
			context.setVariable("errorMessages", errorMessages);
			session.removeAttribute("errorMessages");
		}

		if (loggedInUser.getRole() == Role.CLIENT)
			templateEngine.process("views/index", context, response.getWriter());

		Admin loggedUser = (Admin) loggedInUser;

		int page = 1; // default page is 1
		String pageParam = request.getParameter("page");

		if (pageParam != null) {
			try {
				page = Integer.parseInt(pageParam);
			} catch (NumberFormatException e) {
				logger.error("Error parsing pageParam", e);
				templateEngine.process("views/dashboard/admin/users", context, response.getWriter());
			}
		}

		List<User> users = userService.getAllUsers(page, loggedUser.getLevelAccess());
		int totalPages = userService.getTotalPageNumber(loggedUser.getLevelAccess());

		context.setVariable("user", loggedUser);
		context.setVariable("users", users);
		context.setVariable("totalPages", totalPages);
		context.setVariable("pageNumber", page);

		// Set content type for the response
		response.setContentType("text/html;charset=UTF-8");

		templateEngine.process("views/dashboard/admin/users", context, response.getWriter());
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
				case "get":
					long userId = Long.parseLong(request.getParameter("user_id"));
					try {
						User user = userService.getUser(userId).orElse(null);
						if (user != null) {
							String jsonResponse = convertUserToJson(user);

							response.setStatus(HttpServletResponse.SC_OK);
							response.setContentType("application/json");
							response.getWriter().write(jsonResponse);
						} else {
							response.setStatus(HttpServletResponse.SC_NOT_FOUND);
							response.getWriter().write("{\"error\": \"User not found\"}");
						}

					} catch (Exception e) {
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						response.setContentType("application/json");
						response.getWriter().write("{\"error\": \"error\"}");
					}
					return;
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
					session.setAttribute("message", "User added successfully!");
				} else {
					session.setAttribute("errorMessages", errors);
				}
				break;

			default:
				break;
		}

	}

	protected void updateUser(HttpServletRequest request, HttpServletResponse response) {
		List<String> errors = new ArrayList<>();
		HttpSession session = request.getSession();
		long userId = 0;
		User user;
		Client client = new Client();
		try {
			userId = Long.parseLong(request.getParameter("userId"));
		} catch (Exception e) {
			errors.add("Error parsing user id");
			session.setAttribute("errorMessages", errors);
			return;
		}

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		user = userService.getUser(userId).orElse(null);

		if (user == null) {
			errors.add("Couldn't find user");
			session.setAttribute("errorMessages", errors);
			return;
		}

		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		if (password != null && !password.trim().isEmpty()) {
			user.setPassword(BCrypt.withDefaults().hashToString(12, password.toCharArray()));
		}

		if (user.getRole() == Role.CLIENT) {
			String addressDelivery = request.getParameter("addressDelivery");
			String paymentMethod = request.getParameter("paymentMethod");
			client = (Client) user;
			client.setAddressDelivery(addressDelivery);
			client.setPaymentMethod(paymentMethod);
		}

		errors.addAll(Validator.validateUser(user.getRole() == Role.CLIENT ? client : user));
		logger.info("Validator errors : " + errors);

		if (errors.isEmpty()) {
			userService.updateUser(user.getRole() == Role.CLIENT ? client : user);
			session.setAttribute("message", "User updated successfully!");
		} else {
			session.setAttribute("errorMessages", errors);
		}
	}

	protected void deleteUser(HttpServletRequest request, HttpServletResponse response) {
		String authorIdString = request.getParameter("id");
		long userId = Integer.parseInt(authorIdString);

		userService.deleteUser(userId);
	}

	private String convertUserToJson(User user) {
		StringBuilder json = new StringBuilder("{");
		json.append("\"id\":").append(user.getId()).append(",");
		json.append("\"firstName\":\"").append(user.getFirstName()).append("\",");
		json.append("\"lastName\":\"").append(user.getLastName()).append("\",");
		json.append("\"email\":\"").append(user.getEmail()).append("\",");
		json.append("\"role\":\"").append(user.getRole().name()).append("\"");

		if (user instanceof Admin) {
			Admin admin = (Admin) user;
			json.append(",\"levelAccess\":\"").append(admin.getLevelAccess()).append("\"");
		}
		if (user instanceof Client) {
			Client client = (Client) user;
			json.append(",\"addressDelivery\":\"").append(client.getAddressDelivery()).append("\"");
			json.append(",\"paymentMethod\":\"").append(client.getPaymentMethod()).append("\"");
		}

		json.append("}");
		return json.toString();
	}
}
