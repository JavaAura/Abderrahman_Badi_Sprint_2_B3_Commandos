package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import model.enums.Role;

@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "First Name shouldn't be null")
	@Column(name = "firstName", nullable = false)
	private String firstName;
	
	@NotNull(message = "Last Name shouldn't be null")
	@Column(name="lastName",nullable = false)
	private String lastName;
	
	@NotNull(message = "Email  shouldn't be null")
	@Column(name="email", nullable = false , unique = true)
	private String email;
	
	@NotNull(message = "Password  shouldn't be null")
	@Column(name="password",nullable = false)
	private String password;
	
	@NotNull(message = "Role  shouldn't be null")
	@Enumerated(EnumType.STRING) 
	@Column(name="role", nullable = false , columnDefinition= "ENUM('ADMIN','CLIENT')")
	private Role role ;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	
	

}
