package com.bjs.bjsapi.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

import com.bjs.bjsapi.database.model.Class;

@RepositoryRestResource(collectionResourceRel = "classes", path = "classes")
public interface ClassRepository extends CrudRepository<Class, Long> {

	@PostFilter("hasAuthority('ROLE_ADMIN') or hasPermission(filterObject,'read')")
	List<Class> findAll();

	@PostAuthorize("hasAuthority('ROLE_ADMIN') or hasPermission(returnObject.orElse(null),'read')")
	Optional<Class> findByClassName(String className);

	@PostAuthorize("hasAuthority('ROLE_ADMIN') or hasPermission(returnObject.orElse(null),'read')")
	Optional<Class> findById(Long id);

	@PostFilter("hasAuthority('ROLE_ADMIN') or hasPermission(filterObject,'read')")
	List<Class> findByClassTeacherName(String classTeacherName);

}
