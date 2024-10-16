package service;

import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repository.interfaces.OrderRepository;
import service.OrderService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderService orderService;
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderService = new OrderService(orderRepository);
    }

    @Test
    void testGetOrdersByClient() {
        Long clientId = 1L;
        int page = 0;
        int size = 10;
        String searchQuery = "test";
        
        Order order1 = new Order(); // Create sample orders
        Order order2 = new Order();
        List<Order> expectedOrders = Arrays.asList(order1, order2);
        
        when(orderRepository.getByClient(clientId, page, size, searchQuery)).thenReturn(expectedOrders);
        
        List<Order> actualOrders = orderService.getOrdersByClient(clientId, page, size, searchQuery);
        
        assertEquals(expectedOrders, actualOrders);
        verify(orderRepository, times(1)).getByClient(clientId, page, size, searchQuery);
    }

    @Test
    void testGetAllOrders() {
        int page = 0;
        int size = 10;

        Order order = new Order(); // Create a sample order
        List<Order> expectedOrders = Collections.singletonList(order);

        when(orderRepository.getdAll(page, size)).thenReturn(expectedOrders);

        List<Order> actualOrders = orderService.getAllOrders(page, size);

        assertEquals(expectedOrders, actualOrders);
        verify(orderRepository, times(1)).getdAll(page, size);
    }

    @Test
    void testCreateOrder() {
        Order order = new Order(); // Create a sample order
        
        when(orderRepository.create(order)).thenReturn(order);
        
        Order actualOrder = orderService.createOrder(order);
        
        assertEquals(order, actualOrder);
        verify(orderRepository, times(1)).create(order);
    }

    @Test
    void testUpdateOrder() {
        Order order = new Order(); // Create a sample order
        
        when(orderRepository.update(order)).thenReturn(order);
        
        Order actualOrder = orderService.updateOrder(order);
        
        assertEquals(order, actualOrder);
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
        Order expectedOrder = new Order(); // Create a sample order
        
        when(orderRepository.getById(orderId)).thenReturn(expectedOrder);
        
        Order actualOrder = orderService.getOrderById(orderId);
        
        assertEquals(expectedOrder, actualOrder);
        verify(orderRepository, times(1)).getById(orderId);
    }

    @Test
    void testCanModifyOrder() {
        Order order = new Order(); // Create a sample order
        
        when(orderRepository.canModify(order)).thenReturn(true);
        
        boolean result = orderService.canModifyOrder(order);
        
        assertTrue(result);
        verify(orderRepository, times(1)).canModify(order);
    }
}
