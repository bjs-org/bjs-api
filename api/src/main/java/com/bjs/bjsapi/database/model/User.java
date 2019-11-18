package com.bjs.bjsapi.database.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.relational.core.mapping.Table;

@Entity
@Table("users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	private Boolean enabled = true;

	private boolean administrator = false;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public boolean getAdministrator() {
		return administrator;
	}

	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}

	public Long getId() {
		return id;
	}

	public User() {
	}

	public User(String username, String password, Boolean enabled, Boolean administrator) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.administrator = administrator;
	}

	public User(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return String.format("User{id=%d, username='%s', password='%s', enabled=%s, administrator=%s}", id, username, password, enabled, administrator);
	}

}
