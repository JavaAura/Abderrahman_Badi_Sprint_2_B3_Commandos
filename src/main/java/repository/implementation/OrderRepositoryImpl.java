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
        TypedQuery<Order> query = entityManager.createQuery("SELECT o FROM Order o", Order.class);
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
        return entityManager.merge(order);
    }

    @Override
    public void delete(Long orderId) {
        Order order = getById(orderId);
        if (order != null) {
            entityManager.remove(order);
        }
    }

    @Override
    public Order getById(Long orderId) {
        return entityManager.find(Order.class, orderId);
    }

    @Override
    public boolean canModify(Order order) {

        return false;
    }
}
