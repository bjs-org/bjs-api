package com.bjs.bjsapi.security.evaluators;

import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.UserPrivilege;
import com.bjs.bjsapi.database.repository.UserPrivilegeRepository;

@Component
public class ClassPermissionEvaluator {

	private final UserPrivilegeRepository userPrivilegeRepository;

	public ClassPermissionEvaluator(UserPrivilegeRepository userPrivilegeRepository) {
		this.userPrivilegeRepository = userPrivilegeRepository;
	}

	public boolean hasPermission(Authentication authentication, Class schoolClass, String permission) {
		if (authentication == null || permission == null || schoolClass == null) {
			return false;
		} else {
			List<UserPrivilege> userPrivileges = runAsAdmin(() -> userPrivilegeRepository.findByAccessibleClass(schoolClass));

			return userPrivileges.stream().map(userPrivilege -> userPrivilege.getUser().getUsername()).anyMatch(username -> username.equals(authentication.getName()));
		}
	}

}
