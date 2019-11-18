package com.bjs.bjsapi.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.UserPrivilege;

@RepositoryRestResource(path = "user_privileges", collectionResourceRel = "user_privileges")
public interface UserPrivilegeRepository extends CrudRepository<UserPrivilege, Long> {

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	List<UserPrivilege> findAll();

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	Optional<UserPrivilege> findById(Long id);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	List<UserPrivilege> findByUser(User user);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	List<UserPrivilege> findByAccessibleClass(Class accessibleClass);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	<S extends UserPrivilege> S save(S entity);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void delete(UserPrivilege entity);

}