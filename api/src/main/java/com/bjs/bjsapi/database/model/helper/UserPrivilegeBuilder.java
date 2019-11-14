package com.bjs.bjsapi.database.model.helper;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.UserPrivilege;

public class UserPrivilegeBuilder {

	private User user;
	private Class accessibleClass;

	public UserPrivilegeBuilder setUser(User user) {
		this.user = user;
		return this;
	}

	public UserPrivilegeBuilder setAccessibleClass(Class accessibleClass) {
		this.accessibleClass = accessibleClass;
		return this;
	}

	public UserPrivilege createUserPrivilege() {
		return new UserPrivilege(user, accessibleClass);
	}

}