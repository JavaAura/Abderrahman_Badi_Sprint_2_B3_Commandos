package service;

import model.Order;
import model.User;
import model.enums.Statut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.interfaces.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    public List<Order> getOrdersByClient(Long clientId, int page, int size) {
        return orderRepository.getByClient(clientId, page, size);
    }


    public List<Order> getAllOrders(int page, int size) {
        return orderRepository.getAll(page, size);
    }


    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }


    public Order updateOrder(Order order, User user) {
        return orderRepository.update(order, user);
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



    public List<Order> getAllOrders_No_historique(long clientId,int page, int size) {
        List<Order> list = orderRepository.getByClient(clientId, page, size);

        list.forEach(order -> logger.info("Order : " + order.toString()));

        list.forEach(order -> {
            System.out.println("order: " + order.getId() +
                    ", Order Date: " + order.getOrderDate() +
                    ", Status: " + order.getOrderStatut());
        });

        return list.stream()
                .filter(order -> (order.getClient().getId() == clientId) &&
                        (order.getOrderStatut() == Statut.WAITING || order.getOrderStatut() == Statut.PROCESSING ||
                                order.getOrderStatut() == Statut.SHIPPED))
                .collect(Collectors.toList());
    }



    public List<Order> getAllOrderHistorique(int page, int size) {
        List<Order> list = orderRepository.getAll(page, size);

        return list.stream()
                .filter(order -> order.getOrderStatut() == Statut.DELIVERED ||
                order.getOrderStatut() == Statut.CANCELED)
                .collect(Collectors.toList());
    }


    public int getTotalOrderCount() {
        return  orderRepository.getTotalOrderCount();
    }

    public int getTotalOrderCountByStatus(User user){
        return  orderRepository.getClientTotalOrderCountByStatus(user);
    }
}
