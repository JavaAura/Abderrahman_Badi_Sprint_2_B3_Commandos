package repository.implementation;

import model.Order;
import model.enums.Statut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.interfaces.OrderRepository;
import util.PersistenceUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {

    private static final Logger logger = LoggerFactory.getLogger(OrderRepositoryImpl.class);


    @Override
    public List<Order> getByClient(Long clientId, int page, int size) {
        int startIndex = (page - 1) * size;
        logger.info("Client Id : " + clientId + " page : " + page + " size : " + size);
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            String queryStr = "SELECT o FROM Order o WHERE o.client.id = :clientId";
            TypedQuery<Order> query = entityManager.createQuery(queryStr, Order.class);
            query.setParameter("clientId", clientId);
            query.setFirstResult(startIndex);
            query.setMaxResults(size);

            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Order> getAll(int page, int size) {
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            String queryStr = "SELECT o FROM Order o";
            TypedQuery<Order> query = entityManager.createQuery(queryStr, Order.class);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }


    @Override
    public Order save(Order order) {
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(order);
            transaction.commit();
            return order;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error saving order: {}", e.getMessage());
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Order update(Order order) {
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            if (canModify(order)) {
                transaction.begin();
                Order updatedOrder = entityManager.merge(order);
                transaction.commit();
                return updatedOrder;
            } else {
                throw new IllegalStateException("Cannot modify an order that has been shipped or beyond.");
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error updating order: {}", e.getMessage());
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void delete(Long orderId) {
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Order order = get(orderId);
            if (order != null && canModify(order)) {
                transaction.begin();
                entityManager.remove(entityManager.contains(order) ? order : entityManager.merge(order));
                transaction.commit();
            } else {
                throw new IllegalStateException("Cannot delete an order that has been shipped or beyond.");
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error deleting order: {}", e.getMessage());
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Order get(Long orderId) {
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            return entityManager.find(Order.class, orderId);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public boolean canModify(Order order) {
        // Use the Statut enum directly for comparison
        return order.getOrderStatut() == Statut.WAITING || order.getOrderStatut() == Statut.PROCESSING;
    }

    @Override
    public int getTotalOrderCount() {
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            String queryStr = "SELECT COUNT(o) FROM Orders o";
            TypedQuery<Long> query = entityManager.createQuery(queryStr, Long.class);
            Long count = query.getSingleResult();
            return count.intValue();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public int getTotalOrderCountByStatus() {
        EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            String queryStr = "SELECT COUNT(o) FROM Order o WHERE o.orderStatut IN (:statusList)";
            TypedQuery<Long> query = entityManager.createQuery(queryStr, Long.class);
            query.setParameter("statusList", Arrays.asList(Statut.WAITING, Statut.PROCESSING, Statut.SHIPPED));
            Long count = query.getSingleResult();
            return count.intValue();
        } finally {
            entityManager.close();
        }
    }



}
