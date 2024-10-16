package service;

import model.Order;
import repository.interfaces.OrderRepository;

import java.util.List;

public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    public List<Order> getOrdersByClient(Long clientId, int page, int size, String searchQuery) {
        return orderRepository.getByClient(clientId, page, size, searchQuery);
    }


    public List<Order> getAllOrders(int page, int size) {
        return orderRepository.getdAll(page, size);
    }


    public Order createOrder(Order order) {
        return orderRepository.create(order);
    }


    public Order updateOrder(Order order) {
        return orderRepository.update(order);
    }


    public void deleteOrder(Long orderId) {
        orderRepository.delete(orderId);
    }


    public Order getOrderById(Long orderId) {
        return orderRepository.getById(orderId);
    }


    public boolean canModifyOrder(Order order) {
        return orderRepository.canModify(order);
    }
}
