package com.bjs.bjsapi.security.evaluators;

import java.io.Serializable;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.security.TargetedPermissionEvaluator;

@Component
public class StudentPermissionEvaluator implements TargetedPermissionEvaluator {

	private final ClassPermissionEvaluator classPermissionEvaluator;

	public StudentPermissionEvaluator(ClassPermissionEvaluator classPermissionEvaluator) {
		this.classPermissionEvaluator = classPermissionEvaluator;
	}

	@Override
	public String getTargetType() {
		return Student.class.getSimpleName();
	}

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		if (authentication == null || permission == null || !(targetDomainObject instanceof Student)) {
			return false;
		} else {
			Student student = (Student) targetDomainObject;
			Class schoolClass = student.getSchoolClass();

			return classPermissionEvaluator.hasPermission(authentication, schoolClass, permission);
		}
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		return false;
	}

}
