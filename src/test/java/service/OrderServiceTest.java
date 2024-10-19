package service;

import model.Order;
import model.enums.Statut;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.interfaces.OrderRepository;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrdersByClient() {
        Long clientId = 1L;
        List<Order> mockOrders = Arrays.asList(new Order(), new Order());
        when(orderRepository.getByClient(clientId, 0, 10, "query")).thenReturn(mockOrders);

        List<Order> orders = orderService.getOrdersByClient(clientId, 0, 10, "query");

        assertEquals(2, orders.size());
        verify(orderRepository, times(1)).getByClient(clientId, 0, 10, "query");
    }

    @Test
    void testCreateOrder() {
        Order order = new Order();
        when(orderRepository.save(order)).thenReturn(order);

        Order createdOrder = orderService.createOrder(order);

        assertNotNull(createdOrder);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testUpdateOrder() {
        Order order = new Order();
        when(orderRepository.update(order)).thenReturn(order);

        Order updatedOrder = orderService.updateOrder(order);

        assertNotNull(updatedOrder);
        verify(orderRepository, times(1)).update(order);
    }

    @Test
    void testDeleteOrder() {
        Long orderId = 1L;

        orderService.deleteOrder(orderId);

        verify(orderRepository, times(1)).delete(orderId);
    }

    @Test
    void testGetOrderById() {
        Long orderId = 1L;
        Order order = new Order();
        when(orderRepository.get(orderId)).thenReturn(order);

        Order fetchedOrder = orderService.getOrderById(orderId);

        assertNotNull(fetchedOrder);
        verify(orderRepository, times(1)).get(orderId);
    }

    @Test
    void testGetAllOrders_No_historique() {
        Order order1 = new Order();
        order1.setOrderStatut(Statut.WAITING);

        Order order2 = new Order();
        order2.setOrderStatut(Statut.PROCESSING);

        Order order3 = new Order();
        order3.setOrderStatut(Statut.SHIPPED);

        Order order4 = new Order();
        order4.setOrderStatut(Statut.DELIVERED);

        List<Order> mockOrders = Arrays.asList(order1, order2, order3, order4);
        when(orderRepository.getAll(0, 10)).thenReturn(mockOrders);

        List<Order> filteredOrders = orderService.getAllOrders_No_historique(0, 10);

        assertEquals(3, filteredOrders.size());
        verify(orderRepository, times(1)).getAll(0, 10);
    }

    @Test
    void testGetAllOrderHistorique() {
        Order order1 = new Order();
        order1.setOrderStatut(Statut.WAITING);

        Order order2 = new Order();
        order2.setOrderStatut(Statut.CANCELED);

        Order order3 = new Order();
        order3.setOrderStatut(Statut.DELIVERED);

        List<Order> mockOrders = Arrays.asList(order1, order2, order3);
        when(orderRepository.getAll(0, 10)).thenReturn(mockOrders);

        List<Order> filteredOrders = orderService.getAllOrderHistorique(0, 10);

        assertEquals(2, filteredOrders.size());
        verify(orderRepository, times(1)).getAll(0, 10);
    }
}
