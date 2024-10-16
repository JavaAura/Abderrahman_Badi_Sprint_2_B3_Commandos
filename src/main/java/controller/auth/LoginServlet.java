package controller.auth;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.User;
import repository.implementation.UserRepositoryImpl;
import repository.interfaces.UserRepository;

import service.UserService;
import util.ThymeleafUtil;

import java.io.IOException;
import java.util.Optional;

public class LoginServlet extends HttpServlet {

	private UserRepository userRepository = new UserRepositoryImpl();
	private UserService userService = new UserService(userRepository);

 

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		login(request, response);

	}

	private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String email = request.getParameter("email");
		String password = request.getParameter("password");

		Optional<User> user = userService.login(email, password);

		if (user.isPresent()) {
			HttpSession session = request.getSession();

			session.setAttribute("user", user.get());
			response.sendRedirect("index.html");
		} else {
			request.setAttribute("Error message", "invalid email or password");
			request.getRequestDispatcher("index.html").forward(request, response);
		}

	}

}
