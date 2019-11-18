package com.bjs.bjsapi.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bjs.bjsapi.database.model.User;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RepositoryRestResource(path = "users", collectionResourceRel = "users")
public interface UserRepository extends CrudRepository<User, Long> {

	@Override
	List<User> findAll();

	Optional<User> findById(Long id);

	Optional<User> findByUsername(String username);

	@Override
	<S extends User> S save(S entity);

	@Override
	void delete(User entity);

}