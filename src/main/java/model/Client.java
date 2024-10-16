package model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "client")
public class Client extends User {

    @NotNull(message = "Delivery Address shouldn't be null")
    @Column(name = "addressDelivery", nullable = true)
    private String addressDelivery;

    @NotNull(message = "Payment Method shouldn't be null")
    @Column(name = "paymentMethod", nullable = true)
    private String paymentMethod;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Order> orders;

    public Client() {
        // Default constructor
    }

    public Client(String addressDelivery, String paymentMethod) {
        this.addressDelivery = addressDelivery;
        this.paymentMethod = paymentMethod;
    }

    public String getAddressDelivery() {
        return addressDelivery;
    }

    public void setAddressDelivery(String addressDelivery) {
        this.addressDelivery = addressDelivery;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(List<Order> value) {
        this.orders = value;
    }
}
