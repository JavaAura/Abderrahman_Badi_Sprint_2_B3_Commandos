package model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;  
import javax.validation.constraints.Min;  

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name shouldn't be null")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Description shouldn't be null")
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull(message = "Price shouldn't be null")
    @Positive(message = "Price must be greater than zero")  
    @Column(name = "price", nullable = false)  
    private double price;

    @NotNull(message = "Stock shouldn't be null")
    @Min(value = 0, message = "Stock must be zero or more")  
    @Column(name = "stock", nullable = false)  
    private double stock;
    
    @ManyToMany(mappedBy = "products")
    private List<Client> clients;


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }
}
