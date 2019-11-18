package com.bjs.bjsapi.database.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.relational.core.mapping.Table;

@Entity
@Table("user_privileges")
public class UserPrivilege {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn
	private User user;

	@ManyToOne
	@JoinColumn
	private Class accessibleClass;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Class getAccessibleClass() {
		return accessibleClass;
	}

	public void setAccessibleClass(Class accessibleClass) {
		this.accessibleClass = accessibleClass;
	}

	public UserPrivilege() {
	}

	public UserPrivilege(User user, Class accessibleClass) {
		this.user = user;
		this.accessibleClass = accessibleClass;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("UserPrivilege{id=%d, user=%s, accessibleClass=%s}", id, user, accessibleClass);
	}

}
