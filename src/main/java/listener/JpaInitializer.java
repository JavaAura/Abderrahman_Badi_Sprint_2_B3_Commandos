package listener;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import util.PersistenceUtil;

public class JpaInitializer implements ServletContextListener {
    private EntityManagerFactory entityManagerFactory;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        entityManagerFactory = PersistenceUtil.getEntityManagerFactory();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

}
