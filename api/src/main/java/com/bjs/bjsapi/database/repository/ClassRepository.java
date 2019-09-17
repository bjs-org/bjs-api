package com.bjs.bjsapi.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bjs.bjsapi.database.model.Class;

@RepositoryRestResource(collectionResourceRel = "classes", path = "classes")
public interface ClassRepository extends CrudRepository<Class, Long> {

	@PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject,'read')")
	List<Class> findAll();

	@PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null),'read')")
	Optional<Class> findByClassName(String className);

	@PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null),'read')")
	Optional<Class> findById(Long id);

	@PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject,'read')")
	List<Class> findByClassTeacherName(String classTeacherName);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity,'read')")
	<S extends Class> S save(S entity);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RestResource(exported = false)
	void delete(Class entity);

}
