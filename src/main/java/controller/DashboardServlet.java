package controller;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import model.User;
import util.ThymeleafUtil;

import java.io.IOException;

public class DashboardServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Get the Thymeleaf template engine
		TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());

		HttpSession session = request.getSession();

		User loggedInUser = (User) session.getAttribute("user");

		// Prepare a Thymeleaf context if you need to pass any model data
		ServletContext servletContext = request.getServletContext();
		WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		context.setVariable("user", loggedInUser);

		// Set content type for the response
		response.setContentType("text/html;charset=UTF-8");

		if(loggedInUser == null){
			templateEngine.process("views/index", context, response.getWriter());
		}

		templateEngine.process("views/dashboard/index", context, response.getWriter());

	}
}
