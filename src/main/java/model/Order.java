package model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import model.enums.Statut;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "orderDate")
	private LocalDate orderDate;

	@NotNull(message = "Order status shouldn't be null")
	@Enumerated(EnumType.STRING)
	@Column(name = "orderStatut", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'WAITING'")
	private Statut orderStatut;

	@JoinColumn(name = "client_id", nullable = false)
	@ManyToOne
	private Client client;

	@ManyToMany
	@JoinTable(name = "order_product", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
	private List<Product> products;

	@Override
	public String toString() {
		return "Order{" +
				"id=" + id +
				", orderDate='" + orderDate + '\'' +
				", orderStatut=" + orderStatut +
				'}';
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	public Statut getOrderStatut() {
		return orderStatut;
	}

	public void setOrderStatut(Statut orderStatut) {
		this.orderStatut = orderStatut;
	}

    public List<Product> getProducts() {
      return this.products;
    }
    public void setProducts(List<Product> value) {
      this.products = value;
    }

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
}
