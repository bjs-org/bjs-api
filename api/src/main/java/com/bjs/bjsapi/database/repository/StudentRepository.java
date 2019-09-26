package com.bjs.bjsapi.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.Student;

@RepositoryRestResource(path = "students", collectionResourceRel = "students")
public interface StudentRepository extends CrudRepository<Student, Long> {

	@PostFilter("hasAuthority('ROLE_ADMIN') or hasPermission(filterObject,'read')")
	List<Student> findAllBySchoolClass(Class schoolClass);

	@PostFilter("hasAuthority('ROLE_ADMIN') or hasPermission(filterObject,'read')")
	List<Student> findByFirstNameAndLastName(String firstName, String lastName);

	@PostFilter("hasAuthority('ROLE_ADMIN') or hasPermission(filterObject,'read')")
	List<Student> findByFirstName(String firstName);

	@PostFilter("hasAuthority('ROLE_ADMIN') or hasPermission(filterObject,'read')")
	List<Student> findByLastName(String lastName);

	@PostAuthorize("hasAuthority('ROLE_ADMIN') or hasPermission(returnObject.orElse(null),'read')")
	Optional<Student> findById(Long id);

	@PostFilter("hasAuthority('ROLE_ADMIN') or hasPermission(filterObject,'read')")
	List<Student> findAll();

}
