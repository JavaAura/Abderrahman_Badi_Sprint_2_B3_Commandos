package repository;

import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.implementation.OrderRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Order> typedQuery;

    @InjectMocks
    private OrderRepositoryImpl orderRepository;

    private Order order;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        order = new Order();
        order.setId(1L);
    }

    @Test
    public void testGetByClient() {
        Long clientId = 1L;
        String searchQuery = "test";
        int page = 0;
        int size = 10;

        when(entityManager.createQuery(anyString(), eq(Order.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("clientId", clientId)).thenReturn(typedQuery);
        when(typedQuery.setParameter("searchQuery", "%" + searchQuery + "%")).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(page * size)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(size)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(order));

        List<Order> orders = orderRepository.getByClient(clientId, page, size, searchQuery);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(order, orders.get(0));
    }

    @Test
    public void testGetAll() {
        int page = 0;
        int size = 10;

        when(entityManager.createQuery(anyString(), eq(Order.class))).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(page * size)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(size)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(order));

        List<Order> orders = orderRepository.getdAll(page, size);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(order, orders.get(0));
    }

    @Test
    public void testCreate() {
        orderRepository.create(order);
        verify(entityManager).persist(order);
    }

    @Test
    public void testUpdate() {
        when(entityManager.merge(order)).thenReturn(order);

        Order updatedOrder = orderRepository.update(order);

        assertNotNull(updatedOrder);
        assertEquals(order, updatedOrder);
        verify(entityManager).merge(order);
    }

    @Test
    public void testDelete() {
        when(entityManager.find(Order.class, 1L)).thenReturn(order);

        orderRepository.delete(1L);

        verify(entityManager).remove(order);
    }

    @Test
    public void testGetById() {
        when(entityManager.find(Order.class, 1L)).thenReturn(order);

        Order foundOrder = orderRepository.getById(1L);

        assertNotNull(foundOrder);
        assertEquals(order, foundOrder);
    }
}
