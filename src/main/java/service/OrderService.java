package service;

import model.Order;
import model.enums.Statut;
import repository.interfaces.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    public List<Order> getOrdersByClient(Long clientId, int page, int size, String searchQuery) {
        return orderRepository.getByClient(clientId, page, size, searchQuery);
    }


    public List<Order> getAllOrders(int page, int size) {
        return orderRepository.getAll(page, size);
    }


    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }


    public Order updateOrder(Order order) {
        return orderRepository.update(order);
    }


    public void deleteOrder(Long orderId) {
        orderRepository.delete(orderId);
    }


    public Order getOrderById(Long orderId) {
        return orderRepository.get(orderId);
    }


    public boolean canModifyOrder(Order order) {
        return orderRepository.canModify(order);
    }



    public List<Order> getAllOrders_No_historique(int page, int size) {
        List<Order> listO = orderRepository.getAll(page, size);

        return listO.stream()
                .filter(order -> order.getOrderStatut() == Statut.WAITING ||
                        order.getOrderStatut() == Statut.PROCESSING ||
                        order.getOrderStatut() == Statut.SHIPPED)
                .collect(Collectors.toList());
    }
    public List<Order> getAllOrderHistorique(int page, int size) {
        List<Order> listO = orderRepository.getAll(page, size);

        return listO.stream()
                .filter(order -> order.getOrderStatut() == Statut.DELIVERED ||
                        order.getOrderStatut() == Statut.CANCELED)
                .collect(Collectors.toList());
    }




}
