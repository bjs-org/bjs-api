package com.bjs.bjsapi.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.Student;

@RepositoryRestResource(path = "students", collectionResourceRel = "students")
public interface StudentRepository extends CrudRepository<Student, Long> {

	List<Student> findAllBySchoolClass(Class schoolClass);

	Optional<Student> findByFirstNameAndLastName(String firstName, String lastName);

	List<Student> findByFirstName(String firstName);

	List<Student> findByLastName(String lastName);

	Optional<Student> findById(Long id);

	List<Student> findAll();

}
