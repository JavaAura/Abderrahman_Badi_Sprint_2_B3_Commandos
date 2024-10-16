package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "admin")
public class Admin extends User {
	
    @NotNull(message = "levelAccess shouldn't be null")
	@Column(name = "levelAccess",nullable = false)
	private int levelAccess;
    
    public Admin() {
        // Default constructor
    }

	public int getLevelAccess() {
		return levelAccess;
	}

	public void setLevelAccess(int levelAccess) {
		this.levelAccess = levelAccess;
	}
    
    
}
