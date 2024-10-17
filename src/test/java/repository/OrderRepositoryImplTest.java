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

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Order> typedQuery;

    @InjectMocks
    private OrderRepositoryImpl orderRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetdAll() {
        // Arrange
        Order order1 = new Order();
        order1.setOrderStatut(Statut.valueOf("En attente"));

        Order order2 = new Order();
        order2.setOrderStatut(Statut.valueOf("En traitement"));

        List<Order> expectedOrders = Arrays.asList(order1, order2);

        when(entityManager.createQuery(anyString(), eq(Order.class))).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedOrders);


        List<Order> result = orderRepository.getdAll(0, 10);


        assertEquals(2, result.size());
        assertEquals("En attente", result.get(0).getOrderStatut());
        assertEquals("En traitement", result.get(1).getOrderStatut());

        verify(entityManager).createQuery(anyString(), eq(Order.class));
        verify(typedQuery).setFirstResult(0);
        verify(typedQuery).setMaxResults(10);
    }

    @Test
    void testCreateOrder() {

        Order order = new Order();
        order.setOrderStatut(Statut.valueOf("En attente"));


        Order result = orderRepository.create(order);


        verify(entityManager).persist(order);
        assertNotNull(result);
    }

    @Test
    void testUpdateOrder_CanModify() {

        Order order = new Order();
        order.setOrderStatut(Statut.valueOf("En attente"));

        when(entityManager.merge(any(Order.class))).thenReturn(order);


        Order result = orderRepository.update(order);


        verify(entityManager).merge(order);
        assertEquals(order, result);
    }

    @Test
    void testUpdateOrder_CannotModify() {

        Order order = new Order();
        order.setOrderStatut(Statut.valueOf("Expédiée"));


        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderRepository.update(order);
        });

        assertEquals("Cannot modify an order that has been shipped or beyond.", exception.getMessage());
    }

    @Test
    void testDeleteOrder_CanModify() {

        Order order = new Order();
        order.setId(1L);
        order.setOrderStatut(Statut.valueOf("En attente"));

        when(entityManager.find(Order.class, 1L)).thenReturn(order);


        orderRepository.delete(1L);

        verify(entityManager).remove(order);
    }

    @Test
    void testDeleteOrder_CannotModify() {

        Order order = new Order();
        order.setId(1L);
        order.setOrderStatut(Statut.valueOf("Expédiée"));

        when(entityManager.find(Order.class, 1L)).thenReturn(order);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderRepository.delete(1L);
        });

        assertEquals("Cannot delete an order that has been shipped or beyond.", exception.getMessage());
    }
}
