package repository.implementation;

import model.Order;
import repository.interfaces.OrderRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Order> getByClient(Long clientId, int page, int size, String searchQuery) {

        String queryStr = "SELECT o FROM Order o WHERE o.client.id = :clientId AND " +
                "(o.orderStatut LIKE :searchQuery OR o.id LIKE :searchQuery)";
        TypedQuery<Order> query = entityManager.createQuery(queryStr, Order.class);
        query.setParameter("clientId", clientId);
        query.setParameter("searchQuery", "%" + searchQuery + "%");


        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public List<Order> getdAll(int page, int size) {
        String queryStr = "SELECT o FROM Order o WHERE o.orderStatut = 'En attente' OR o.orderStatut = 'En traitement'";
        TypedQuery<Order> query = entityManager.createQuery(queryStr, Order.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }


    @Override
    public Order create(Order order) {
        entityManager.persist(order);
        return order;
    }

    @Override
    public Order update(Order order) {
        if (canModify(order)) {
            return entityManager.merge(order);
        } else {
            throw new IllegalStateException("Cannot modify an order that has been shipped or beyond.");
        }
    }

    @Override
    public void delete(Long orderId) {
        Order order = getById(orderId);
        if (order != null && canModify(order)) {
            entityManager.remove(order);
        } else {
            throw new IllegalStateException("Cannot delete an order that has been shipped or beyond.");
        }
    }

    @Override
    public Order getById(Long orderId) {
        return entityManager.find(Order.class, orderId);
    }

    @Override
    public boolean canModify(Order order) {

        return order.getOrderStatut().equals("En attente") || order.getOrderStatut().equals("En traitement");
    }
}
