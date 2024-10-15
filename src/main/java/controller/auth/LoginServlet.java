package controller.auth;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import repository.implementation.UserRepositoryImpl;
import repository.interfaces.UserRepository;

import service.UserService;

import java.io.IOException;

public class LoginServlet extends HttpServlet {

	private UserRepository userRepository = new UserRepositoryImpl();
	private UserService userService = new UserService(userRepository);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

}
