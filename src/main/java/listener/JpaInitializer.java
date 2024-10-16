package listener;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import at.favre.lib.crypto.bcrypt.BCrypt;
import model.Admin;
import model.Client;
import model.Product;
import model.enums.Role;
import util.PersistenceUtil;

public class JpaInitializer implements ServletContextListener {
    private EntityManagerFactory entityManagerFactory;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        entityManagerFactory = PersistenceUtil.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        String plainPassword = "password";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
        try {
            transaction.begin();

            Client client1 = new Client();
            client1.setFirstName("client");
            client1.setLastName("1");
            client1.setEmail("client1@youcode.ma");
            client1.setPassword(hashedPassword);
            client1.setRole(Role.CLIENT);
            client1.setAddressDelivery("client 1 address");
            client1.setPaymentMethod("cash");
            entityManager.persist(client1);

            Client client2 = new Client();
            client2.setFirstName("client");
            client2.setLastName("2");
            client2.setEmail("client2@youcode.ma");
            client2.setPassword(hashedPassword);
            client2.setRole(Role.CLIENT);
            client2.setAddressDelivery("client 2 address");
            client2.setPaymentMethod("cash");
            entityManager.persist(client2);

            Client client3 = new Client();
            client3.setFirstName("client");
            client3.setLastName("3");
            client3.setEmail("client3@youcode.ma");
            client3.setPassword(hashedPassword);
            client3.setRole(Role.CLIENT);
            client3.setAddressDelivery("client 3 address");
            client3.setPaymentMethod("cash");
            entityManager.persist(client3);

            Client client4 = new Client();
            client4.setFirstName("client");
            client4.setLastName("4");
            client4.setEmail("client4@youcode.ma");
            client4.setPassword(hashedPassword);
            client4.setRole(Role.CLIENT);
            client4.setAddressDelivery("client 4 address");
            client4.setPaymentMethod("cash");
            entityManager.persist(client4);

            // ----------------------------------------------------------------

            Admin admin1 = new Admin();
            admin1.setFirstName("Jack");
            admin1.setLastName("Jones");
            admin1.setEmail("admin@youcode.ma");
            admin1.setPassword(hashedPassword);
            admin1.setLevelAccess(1);
            admin1.setRole(Role.ADMIN);
            entityManager.persist(admin1);

            Admin admin2 = new Admin();
            admin2.setFirstName("Bob");
            admin2.setLastName("The technician");
            admin2.setEmail("bob@youcode.ma");
            admin2.setPassword(hashedPassword);
            admin2.setLevelAccess(2);
            admin2.setRole(Role.ADMIN);
            entityManager.persist(admin2);

            Admin admin3 = new Admin();
            admin3.setFirstName("Sophia");
            admin3.setLastName("Havertz");
            admin3.setEmail("sophia@youcode.ma");
            admin3.setPassword(hashedPassword);
            admin3.setLevelAccess(2);
            admin3.setRole(Role.ADMIN);
            entityManager.persist(admin3);

            // ----------------------------------------------------------------

            Product product1 = new Product();
            product1.setName("Product A");
            product1.setDescription("Product A description");
            product1.setPrice(29.99);
            product1.setStock(200);
            entityManager.persist(product1);

            Product product2 = new Product();
            product2.setName("Product B");
            product2.setDescription("Product B description");
            product2.setPrice(19.99);
            product2.setStock(180);
            entityManager.persist(product2);

            Product product3 = new Product();
            product3.setName("Product C");
            product3.setDescription("Product C description");
            product3.setPrice(49.99);
            product3.setStock(250);
            entityManager.persist(product3);

            Product product4 = new Product();
            product4.setName("Product D");
            product4.setDescription("Product D description");
            product4.setPrice(99.99);
            product4.setStock(300);
            entityManager.persist(product4);

            Product product5 = new Product();
            product5.setName("Product E");
            product5.setDescription("Product E description");
            product5.setPrice(5.00);
            product5.setStock(2000);
            entityManager.persist(product5);

            Product product6 = new Product();
            product6.setName("Product F");
            product6.setDescription("Product F description");
            product6.setPrice(24.99);
            product6.setStock(20);
            entityManager.persist(product6);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

}
