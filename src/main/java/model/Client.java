package model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class Client extends User {

    @Column(name = "addressDelivery", nullable = true)
    private String addressDelivery;

    @Column(name = "paymentMethod", nullable = true)
    private String paymentMethod;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Order> orders;

    @Override
	public String toString() {
		return super.toString() +
				", addressDelivery=" + addressDelivery +
				", paymentMethod=" + paymentMethod;
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
