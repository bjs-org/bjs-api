package com.bjs.bjsapi.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.bjs.bjsapi.database.model.Class;

@RepositoryRestResource(collectionResourceRel = "classes", path = "classes")
public interface ClassRepository extends CrudRepository<Class, Long> {

	@Query(value = "SELECT * FROM classes WHERE id IN (SELECT accessible_class_id FROM user_privileges WHERE user_id = (SELECT id FROM users WHERE username=?#{ principal?.username } LIMIT 1))", nativeQuery = true)
	List<Class> findAll();

	Optional<Class> findByClassName(String className);

	Optional<Class> findById(Long id);

	List<Class> findByClassTeacherName(String classTeacherName);

}
