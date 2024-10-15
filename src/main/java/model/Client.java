package model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "client")
@Inheritance(strategy = InheritanceType.JOINED)  
public class Client extends User {  

    @NotNull(message = "Delivery Address shouldn't be null")
    @Column(name = "addressDelivery", nullable = false)
    private String addressDelivery;

    @NotNull(message = "Payment Method shouldn't be null")
    @Column(name = "paymentMethod", nullable = false)
    private String paymentMethod;
    
    @ManyToMany
    @JoinTable(
        name = "order",  
        joinColumns = @JoinColumn(name = "client_id"), // Foreign key to Client
        inverseJoinColumns = @JoinColumn(name = "product_id") // Foreign key to Product
    )
    private List<Product> products;


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
}
