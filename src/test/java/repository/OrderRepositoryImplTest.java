package repository;

import model.Order;
import model.enums.Statut;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderRepositoryImplTest {

    @InjectMocks
    private OrderRepositoryImpl orderRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Order> typedQuery;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetByClient() {
        Long clientId = 1L;
        String searchQuery = "test";
        int page = 0;
        int size = 10;

        Order order1 = new Order();
        order1.setId(1L);
        order1.setOrderStatut(Statut.WAITING);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setOrderStatut(Statut.PROCESSING);

        when(entityManager.createQuery(anyString(), eq(Order.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("clientId", clientId)).thenReturn(typedQuery);
        when(typedQuery.setParameter("searchQuery", "%" + searchQuery + "%")).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(page * size)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(size)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(order1, order2));

        List<Order> orders = orderRepository.getByClient(clientId, page, size, searchQuery);

        assertNotNull(orders);
        assertEquals(2, orders.size());
        verify(entityManager).createQuery(anyString(), eq(Order.class));
        verify(typedQuery).setParameter("clientId", clientId);
        verify(typedQuery).setParameter("searchQuery", "%" + searchQuery + "%");
    }

    @Test
    void testGetAll() {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setOrderStatut(Statut.WAITING);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setOrderStatut(Statut.PROCESSING);

        when(entityManager.createQuery(anyString(), eq(Order.class))).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(order1, order2));

        List<Order> orders = orderRepository.getdAll(0, 10);

        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    @Test
    void testCreate() {
        Order order = new Order();
        order.setId(1L);

        orderRepository.create(order);

        verify(entityManager).persist(order);
    }

    @Test
    void testUpdate() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatut(Statut.WAITING);

        when(entityManager.merge(order)).thenReturn(order);

        Order updatedOrder = orderRepository.update(order);

        assertNotNull(updatedOrder);
        assertEquals(order.getId(), updatedOrder.getId());
        verify(entityManager).merge(order);
    }

    @Test
    void testDelete() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatut(Statut.WAITING);

        when(entityManager.find(Order.class, orderId)).thenReturn(order);

        orderRepository.delete(orderId);

        verify(entityManager).remove(order);
    }

    @Test
    void testGetById() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        when(entityManager.find(Order.class, orderId)).thenReturn(order);

        Order foundOrder = orderRepository.getById(orderId);

        assertNotNull(foundOrder);
        assertEquals(orderId, foundOrder.getId());
    }

    @Test
    void testCanModify() {
        Order order = new Order();
        order.setOrderStatut(Statut.WAITING);

        boolean canModify = orderRepository.canModify(order);

        assertTrue(canModify);
    }
    /*test */
}
