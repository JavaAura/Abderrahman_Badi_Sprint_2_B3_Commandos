package model;

import javax.persistence.Column;
import javax.persistence.Entity;

import model.enums.Role;

@Entity
public class Admin extends User {

	@Column(name = "levelAccess", nullable = true)
	private int levelAccess;

	@Override
	public String toString() {
		return super.toString() +
				", levelAccess=" + levelAccess +
				'}';
	}

	public Admin(){

	}

	public Admin(String firstName, String lastName, String email, String password, Role role){
		super(firstName, lastName, email, password, role);
		this.levelAccess = 2;
	}

	public int getLevelAccess() {
		return levelAccess;
	}

	public void setLevelAccess(int levelAccess) {
		this.levelAccess = levelAccess;
	}
}
