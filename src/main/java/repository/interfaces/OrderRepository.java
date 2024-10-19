package repository.interfaces;

import java.util.List;
import model.Order;
import model.enums.Statut;

public interface OrderRepository {

    List<Order> getByClient(Long clientId, int page, int size);

    List<Order> getAll(int page, int size);

    Order save(Order order);

    Order update(Order order) ;

    void delete(Long orderId);

    Order get(Long orderId);

    boolean canModify(Order order);

    public int getTotalOrderCount();
    public int getTotalOrderCountByStatus();

}
