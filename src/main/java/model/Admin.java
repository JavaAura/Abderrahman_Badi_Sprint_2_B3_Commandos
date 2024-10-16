package model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Admin extends User {

	@Column(name = "levelAccess", nullable = true)
	private int levelAccess;

	@Override
	public String toString() {
		return super.toString() +
				", levelAccess=" + levelAccess;
	}

	public int getLevelAccess() {
		return levelAccess;
	}

	public void setLevelAccess(int levelAccess) {
		this.levelAccess = levelAccess;
	}
}
