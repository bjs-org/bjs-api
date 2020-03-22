package com.bjs.bjsapi.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.UserPrivilege;

@Service
public class UserAccessibleClassService {

	public List<Class> getAccessibleClassesByUser(User user) {
		return user
			.getUserPrivileges()
			.stream()
			.map(UserPrivilege::getAccessibleClass)
			.collect(Collectors.toList());
	}

}
