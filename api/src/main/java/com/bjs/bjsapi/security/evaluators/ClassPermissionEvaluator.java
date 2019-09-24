package com.bjs.bjsapi.security.evaluators;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.UserPrivilege;
import com.bjs.bjsapi.database.repository.UserPrivilegeRepository;
import com.bjs.bjsapi.security.TargetedPermissionEvaluator;

@Component
public class ClassPermissionEvaluator implements TargetedPermissionEvaluator {

	private final UserPrivilegeRepository userPrivilegeRepository;

	public ClassPermissionEvaluator(UserPrivilegeRepository userPrivilegeRepository) {
		this.userPrivilegeRepository = userPrivilegeRepository;
	}

	@Override
	public String getTargetType() {
		return Class.class.getSimpleName();
	}

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		if (authentication == null || permission == null || !(targetDomainObject instanceof Class)) {
			return false;
		} else {
			Class domainObject = (Class) targetDomainObject;
			List<UserPrivilege> userPrivileges = userPrivilegeRepository.findByAccessibleClass(domainObject);

			return userPrivileges.stream().map(userPrivilege -> userPrivilege.getUser().getUsername()).anyMatch(username -> username.equals(authentication.getName()));
		}
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		return false;
	}

}
