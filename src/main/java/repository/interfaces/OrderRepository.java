package repository.interfaces;

import java.util.List;
import model.Order;
import model.enums.Statut;

public interface OrderRepository {

    List<Order> findOrdersByClient(Long clientId, int page, int size, String searchQuery);

    List<Order> findAllOrders(int page, int size);

    Order createOrder(Order order);

    Order updateOrder(Order order) ;

    void deleteOrder(Long orderId);

    Order findOrderById(Long orderId);

    boolean canModifyOrder(Order order);
}
