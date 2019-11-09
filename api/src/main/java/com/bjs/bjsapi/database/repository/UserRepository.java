package com.bjs.bjsapi.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bjs.bjsapi.database.model.User;

@RepositoryRestResource(path = "users", collectionResourceRel = "users")
public interface UserRepository extends CrudRepository<User, Long> {

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	List<User> findAll();

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	Optional<User> findById(Long id);

	@RestResource(exported = false)
	Optional<User> findByUsername(String username);

}