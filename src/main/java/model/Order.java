package model;

import java.time.LocalDate;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import model.enums.Statut;

@Entity
@Table(name = "order")

public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "orderDate")
	private LocalDate orderDate;

	@NotNull(message = "Role  shouldn't be null")
	@Enumerated(EnumType.STRING)
	@Column(name = "orderStatut", nullable = false, columnDefinition = "ENUM('WAITING','PROCESSING','SHIPPED','DELIVERED','CANCELED')")
	private Statut orderStatut;

	@ManyToOne
	@JoinColumn(name = "client_id", nullable = false)
	private Client clientId;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product productId;

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

	public Client getClientId() {
		return clientId;
	}

	public void setClientId(Client clientId) {
		this.clientId = clientId;
	}

	public Product getProductId() {
		return productId;
	}

	public void setProductId(Product productId) {
		this.productId = productId;
	}
	
	
}
