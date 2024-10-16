package util;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;

public class ThymeleafUtil {

    private static TemplateEngine templateEngine;

    public static TemplateEngine getTemplateEngine(ServletContext servletContext) {
        if (templateEngine == null) {
            ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
            templateResolver.setPrefix("/WEB-INF/templates/"); // Path to your templates folder
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode("HTML5");

            templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);
        }
        return templateEngine;
    }
}
