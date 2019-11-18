package com.bjs.bjsapi.security.evaluators;

import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.UserPrivilege;
import com.bjs.bjsapi.database.repository.ClassRepository;
import com.bjs.bjsapi.database.repository.UserPrivilegeRepository;

@Component
public class ClassPermissionEvaluator {

	private final UserPrivilegeRepository userPrivilegeRepository;
	private final ClassRepository classRepository;

	public ClassPermissionEvaluator(UserPrivilegeRepository userPrivilegeRepository, ClassRepository classRepository) {
		this.userPrivilegeRepository = userPrivilegeRepository;
		this.classRepository = classRepository;
	}

	public boolean hasPermission(Authentication authentication, Class schoolClass, String permission) {
		if (authentication == null || permission == null || schoolClass == null) {
			return false;
		} else {
			if (schoolClass.getId() != null && classRepository.existsById(schoolClass.getId())) {
				List<UserPrivilege> userPrivileges = runAsAdmin(() -> userPrivilegeRepository.findByAccessibleClass(schoolClass));

				return userPrivileges.stream().map(userPrivilege -> userPrivilege.getUser().getUsername()).anyMatch(username -> username.equals(authentication.getName()));
			} else {
				return false;
			}
		}
	}

}
