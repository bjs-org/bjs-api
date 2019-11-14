package com.bjs.bjsapi.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.UserPrivilege;

@RepositoryRestResource(path = "user_privileges", collectionResourceRel = "user_privileges", exported = false)
public interface UserPrivilegeRepository extends CrudRepository<UserPrivilege, Long> {

	@Override
	List<UserPrivilege> findAll();

	Optional<UserPrivilege> findById(Long id);

	List<UserPrivilege> findByUser(User user);

	List<UserPrivilege> findByAccessibleClass(Class accessibleClass);

	@Override
	<S extends UserPrivilege> S save(S entity);

	@Override
	void delete(UserPrivilege entity);

}