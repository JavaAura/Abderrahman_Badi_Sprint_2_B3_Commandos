package repository.interfaces;

import java.util.List;
import model.Order;
import model.enums.Statut;

public interface OrderRepository {

    List<Order> getByClient(Long clientId, int page, int size, String searchQuery);

    List<Order> getdAll(int page, int size);

    Order create(Order order);

    Order update(Order order) ;

    void delete(Long orderId);

    Order getById(Long orderId);

    boolean canModify(Order order);
}
