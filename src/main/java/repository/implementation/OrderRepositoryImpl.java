package repository.implementation;

import model.Order;
import repository.interfaces.OrderRepository;
import util.PersistenceUtil;

import javax.persistence.EntityManager;

import javax.persistence.TypedQuery;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {



    @Override
    public List<Order> getByClient(Long clientId, int page, int size, String searchQuery) {
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();

        String queryStr = "SELECT o FROM Order o WHERE o.client.id = :clientId AND " +
                "(o.orderStatut LIKE :searchQuery OR o.id LIKE :searchQuery)";
        TypedQuery<Order> query = entityManager.createQuery(queryStr, Order.class);
        query.setParameter("clientId", clientId);


        if (searchQuery == null) {
            searchQuery = ""; 
        }

        query.setParameter("searchQuery", "%" + searchQuery + "%");
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public List<Order> getAll(int page, int size) {
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        String queryStr = "SELECT o FROM Order o";
        TypedQuery<Order> query = entityManager.createQuery(queryStr, Order.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public Order save(Order order) {
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        entityManager.persist(order);
        return order;
    }

    @Override
    public Order update(Order order) {
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        if (canModify(order)) {
            return entityManager.merge(order);
        } else {
            throw new IllegalStateException("Cannot modify an order that has been shipped or beyond.");
        }
    }

    @Override
    public void delete(Long orderId) {
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        Order order = get(orderId);
        if (order != null && canModify(order)) {
            entityManager.remove(order);
        } else {
            throw new IllegalStateException("Cannot delete an order that has been shipped or beyond.");
        }
    }

    @Override
    public Order get(Long orderId) {
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        return entityManager.find(Order.class, orderId);
    }

    @Override
    public boolean canModify(Order order) {
        return order.getOrderStatut().equals("WAITING") || order.getOrderStatut().equals("PROCESSING");
    }
}
