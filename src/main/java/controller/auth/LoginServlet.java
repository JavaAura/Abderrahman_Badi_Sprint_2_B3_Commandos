package controller.auth;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import repository.implementation.UserRepositoryImpl;
import repository.interfaces.UserRepository;
import service.UserService;

import java.io.IOException;

public class LoginServlet extends HttpServlet {
	private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);

    private UserRepository userRepository = new UserRepositoryImpl();
    private UserService userService = new UserService(userRepository);

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            logout(request, response);
        } else {
            login(request, response);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = userService.login(email, password).orElse(null);

        HttpSession session = request.getSession();
        if (user != null) {
            if(user.getIsDeleted()){
                session.setAttribute("errorMessage", "Account is locked or banned, please contact suport for more info.");
            }else{
                session.setAttribute("user", user);
            }

        } else {
            session.setAttribute("errorMessage", "Invalid email or password.");
        }
        response.sendRedirect("/Commandos");
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);  
        if (session != null) {
            session.invalidate();  
        }
        
        response.sendRedirect(request.getContextPath() + "/");  
    }

}
