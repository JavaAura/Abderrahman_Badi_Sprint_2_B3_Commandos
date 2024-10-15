package repository.implementation;

import model.Order;
import repository.interfaces.OrderRepository;

import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public List<Order> findOrdersByClient(Long clientId, int page, int size, String searchQuery) {
        return null;
    }

    @Override
    public List<Order> findAllOrders(int page, int size) {
        return null;
    }

    @Override
    public Order createOrder(Order order) {
        return null;
    }

    @Override
    public Order updateOrder(Order order) {
        return null;
    }

    @Override
    public void deleteOrder(Long orderId) {

    }

    @Override
    public Order findOrderById(Long orderId) {
        return null;
    }

    @Override
    public boolean canModifyOrder(Order order) {
        return false;
    }
}
