package controller;

import java.io.IOException;

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

import model.User;
import util.ThymeleafUtil;

public class HomeServlet extends HttpServlet {
	private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the Thymeleaf template engine
        TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());

        HttpSession session = request.getSession();

        User loggedInUser = (User) session.getAttribute("user");

        String message = (String) session.getAttribute("errorMessage");

        // Prepare a Thymeleaf context if you need to pass any model data
        ServletContext servletContext = request.getServletContext();
        WebContext context = new WebContext(request, response, servletContext, request.getLocale());

        if (message != null) {
            context.setVariable("errorMessage", message);
            session.removeAttribute("errorMessage");
        }

        context.setVariable("user", loggedInUser);

        // Set content type for the response
        response.setContentType("text/html;charset=UTF-8");

        templateEngine.process("views/index", context, response.getWriter());
    }
}
