package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import util.ThymeleafUtil;

public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get the Thymeleaf template engine
        TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine(request.getServletContext());
        
        // Prepare a Thymeleaf context if you need to pass any model data
        Context context = new Context();
        
        // Set content type for the response
        response.setContentType("text/html;charset=UTF-8");
        
        // Process the home.html template located in templates/views/
        templateEngine.process("views/home", context, response.getWriter());
    }
}
