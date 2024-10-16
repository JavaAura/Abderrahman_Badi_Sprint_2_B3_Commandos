package repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Admin;
import model.Client;
import model.User;
import model.enums.Role;
import repository.implementation.UserRepositoryImpl;
import repository.interfaces.UserRepository;
import util.PersistenceUtil;

public class UserRepositoryImplTest {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImplTest.class);

    private EntityManager entityManager;

    private UserRepository userRepository;

    private Client client;

    private Admin superAdmin;
    private Admin admin;

    @BeforeClass
    public static void setUpBeforeClass() {
        System.setProperty("persistence.unit.name", "test_COMMANDOS_PU");
    }

    @Before
    public void setUp() {
        userRepository = new UserRepositoryImpl();
        client = new Client();
        client.setFirstName("client");
        client.setLastName("1");
        client.setEmail("client@youcode.ma");
        client.setPassword("password");
        client.setRole(Role.CLIENT);
        client.setAddressDelivery("client 1 address");
        client.setPaymentMethod("cash");

        superAdmin = new Admin();
        superAdmin.setFirstName("Super");
        superAdmin.setLastName("Admin");
        superAdmin.setEmail("superAdmin@youcode.ma");
        superAdmin.setPassword("password");
        superAdmin.setLevelAccess(1);
        superAdmin.setRole(Role.ADMIN);

        admin = new Admin();
        admin.setFirstName("Normal");
        admin.setLastName("Admin");
        admin.setEmail("admin@youcode.ma");
        admin.setPassword("password");
        admin.setLevelAccess(2);
        admin.setRole(Role.ADMIN);

        entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.persist(client);
        entityManager.persist(superAdmin);
        entityManager.persist(admin);

        transaction.commit();
        entityManager.close();
    }

    @Test
    public void getAllUsersTest() {
        List<User> users = userRepository.getAll(1);
        assertTrue("Users list is empty", users.size() == 2);
        users.forEach(user -> System.out.println(user));
        assertNotNull(users);
    }

    @Test
    public void getAllClientsTest() {
        List<User> users = userRepository.getAllClients(1);
        logger.info("Fetched clients : " + users.get(0).getEmail());
        assertTrue("Clients list is empty", users.size() == 1);
        assertNotNull(users);
    }

    @Test
    public void addUserTest() {
        Client addedClient = new Client();
        addedClient.setFirstName("added client");
        addedClient.setLastName("1");
        addedClient.setEmail("testclient@youcode.ma");
        addedClient.setPassword("password");
        addedClient.setRole(Role.CLIENT);
        addedClient.setAddressDelivery("client 1 address");
        addedClient.setPaymentMethod("cash");

        userRepository.save(addedClient);

        User user = userRepository.get(addedClient.getId()).orElse(null);

        assertNotNull(user);
        assertTrue("Failed asserting added client name", "added client".equals(user.getFirstName()));
    }

    @Test
    public void updateUserTest() {
        Client updatedClient = new Client();
        updatedClient.setFirstName("client to be updated");
        updatedClient.setLastName("1");
        updatedClient.setEmail("testclient@youcode.ma");
        updatedClient.setPassword("password");
        updatedClient.setRole(Role.CLIENT);

        userRepository.save(updatedClient);

        updatedClient.setFirstName("client name updated");

        userRepository.update(updatedClient);

        User user = userRepository.get(updatedClient.getId()).orElse(null);

        assertNotNull(user);
        assertTrue("Failed asserting updated client name", "client name updated".equals(user.getFirstName()));
        assertFalse("Failed asserting old client name", "client to be updated".equals(user.getFirstName()));
    }

    @Test
    public void deleteUserTest(){
        Client deletedClient = new Client();
        deletedClient.setFirstName("client to be removed");
        deletedClient.setLastName("1");
        deletedClient.setEmail("testclient@youcode.ma");
        deletedClient.setPassword("password");
        deletedClient.setRole(Role.CLIENT);

        userRepository.save(deletedClient);

        userRepository.delete(deletedClient.getId());

        User user = userRepository.get(deletedClient.getId()).orElse(null);

        assertNotNull(user);
        assertTrue("Failed to update is_deleted of user", user.getIsDeleted());

    }
}
