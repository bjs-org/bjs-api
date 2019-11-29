package com.bjs.bjsapi.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.enums.DisciplineType;

@RepositoryRestResource(collectionResourceRel = "sport_results", path = "sport_results")
public interface SportResultRepository extends CrudRepository<SportResult, Long> {

	List<SportResult> findAll();

	List<SportResult> findByStudent(Student student);

	Optional<SportResult> findById(Long id);

	List<SportResult> findByDiscipline(DisciplineType discipline);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or @sportResultPermissionEvaluator.hasPermission(authentication,#entity,'write')")
	<S extends SportResult> S save(S entity);

}
