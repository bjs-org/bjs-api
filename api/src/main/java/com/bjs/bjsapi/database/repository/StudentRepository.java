package com.bjs.bjsapi.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.Student;

@RepositoryRestResource(path = "students", collectionResourceRel = "students")
public interface StudentRepository extends CrudRepository<Student, Long> {

	@PostFilter("hasRole('ROLE_ADMIN') or @studentPermissionEvaluator.hasPermission(authentication,filterObject,'read')")
	List<Student> findAllBySchoolClass(Class schoolClass);

	@PostFilter("hasRole('ROLE_ADMIN') or @studentPermissionEvaluator.hasPermission(authentication,filterObject,'read')")
	List<Student> findByFirstNameAndLastName(String firstName, String lastName);

	@PostFilter("hasRole('ROLE_ADMIN') or @studentPermissionEvaluator.hasPermission(authentication,filterObject,'read')")
	List<Student> findByFirstName(String firstName);

	@PostFilter("hasRole('ROLE_ADMIN') or @studentPermissionEvaluator.hasPermission(authentication,filterObject,'read')")
	List<Student> findByLastName(String lastName);

	@PostAuthorize("hasRole('ROLE_ADMIN') or @studentPermissionEvaluator.hasPermission(authentication,returnObject.orElse(null),'read')")
	Optional<Student> findById(Long id);

	@PostFilter("hasRole('ROLE_ADMIN') or @studentPermissionEvaluator.hasPermission(authentication,filterObject,'read')")
	List<Student> findAll();

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or @studentPermissionEvaluator.hasPermission(authentication,#entity,'write')")
	<S extends Student> S save(S entity);

}
