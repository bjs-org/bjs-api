package com.bjs.bjsapi.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.UserPrivilege;

@RepositoryRestResource(collectionResourceRel = "user_privileges", path = "user_privileges")
public interface UserPrivilegeRepository extends CrudRepository<UserPrivilege, Long> {

	Optional<UserPrivilege> findById(Long id);

	List<UserPrivilege> findByUser(User user);

	List<UserPrivilege> findAll();

	List<UserPrivilege> findByAccessibleClass(Class accessibleClass);

}
