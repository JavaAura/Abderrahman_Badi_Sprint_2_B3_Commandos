package repository.implementation;

import model.Order;
import repository.interfaces.OrderRepository;

import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {


    @Override
    public List<Order> findByClient(Long clientId, int page, int size, String searchQuery) {
        return null;
    }

    @Override
    public List<Order> findAll(int page, int size) {
        return null;
    }

    @Override
    public Order create(Order order) {
        return null;
    }

    @Override
    public Order update(Order order) {
        return null;
    }

    @Override
    public void delete(Long orderId) {

    }

    @Override
    public Order findById(Long orderId) {
        return null;
    }

    @Override
    public boolean canModify(Order order) {
        return false;
    }
}
