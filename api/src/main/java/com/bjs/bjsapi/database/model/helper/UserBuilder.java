package com.bjs.bjsapi.database.model.helper;

import com.bjs.bjsapi.database.model.User;

public class UserBuilder {

	private String username;
	private String password;
	private Boolean enabled = true;
	private boolean administrator = false;

	public UserBuilder setUsername(String username) {
		this.username = username;
		return this;
	}

	public UserBuilder setPassword(String password) {
		this.password = password;
		return this;
	}

	public UserBuilder setEnabled(Boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public UserBuilder setAdministrator(boolean administrator) {
		this.administrator = administrator;
		return this;
	}

	public User createUser() {
		return new User(username, password, enabled, administrator);
	}

}