package service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import model.Admin;
import model.Client;
import model.User;
import model.enums.Role;
import repository.interfaces.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private Client client;
    private List<User> userList = new ArrayList<>();

    @Before
    public void setUp() {

        client = new Client();
        client.setFirstName("client test");
        client.setLastName("1");
        client.setEmail("client@youcode.ma");
        client.setPassword("password");
        client.setRole(Role.CLIENT);
        client.setAddressDelivery("client 1 address");
        client.setPaymentMethod("cash");

        Client client1 = new Client();
        client1.setFirstName("client");
        client1.setLastName("1");
        client1.setEmail("client1@youcode.ma");
        client1.setPassword("password");
        client1.setRole(Role.CLIENT);
        client1.setAddressDelivery("client 1 address");
        client1.setPaymentMethod("cash");

        Client client2 = new Client();
        client2.setFirstName("client");
        client2.setLastName("2");
        client2.setEmail("client2@youcode.ma");
        client2.setPassword("password");
        client2.setRole(Role.CLIENT);
        client2.setAddressDelivery("client 2 address");
        client2.setPaymentMethod("cash");

        userList.add(client1);
        userList.add(client2);

        userService = new UserService(userRepository);
    }

    @Test
    public void getUserByIdTest() {

        // Arrange-Act-Assert (AAA) pattern for mocking tests
        when(userRepository.get(1L)).thenReturn(Optional.of(client));

        Optional<User> result = userService.getUser(1L);

        assertTrue(result.isPresent());
        assertEquals("client@youcode.ma", result.get().getEmail());

        verify(userRepository).get(1L);
    }

    @Test
    public void getAllUsersTest() {
        when(userRepository.getAll(1)).thenReturn(userList);

        List<User> result = userService.getAllUsers(1);

        assertEquals(2, result.size());
        assertEquals("client1@youcode.ma", result.get(0).getEmail());
        assertEquals("client2@youcode.ma", result.get(1).getEmail());

        verify(userRepository).getAll(1);
    }

    @Test
    public void getAllClientsTest() {
        when(userRepository.getAll(1)).thenReturn(userList);

        List<User> result = userService.getAllUsers(1);

        assertEquals(2, result.size());
        assertEquals("client1@youcode.ma", result.get(0).getEmail());
        assertEquals("client2@youcode.ma", result.get(1).getEmail());

        verify(userRepository).getAll(1);
    }

    @Test
    public void saveUserTest() {

        Admin admin = new Admin();
        admin.setId(1L);
        admin.setFirstName("new user");

        userService.addUser(admin);

        verify(userRepository).save(admin);
    }

    @Test
    public void updateUserTest() {

        User userToUpdate = new User();
        userToUpdate.setId(1L);
        userToUpdate.setFirstName("updated user");

        userService.updateUser(userToUpdate);

        verify(userRepository).update(userToUpdate);
    }

    @Test
    public void deleteUserTest() {

        User userToDelete = new User();
        userToDelete.setId(1L);
        userToDelete.setFirstName("new user");

        userService.deleteUser(userToDelete.getId());

        verify(userRepository).delete(userToDelete.getId());
    }
}
