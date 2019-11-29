package com.bjs.bjsapi.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.enums.DisciplineType;

@RepositoryRestResource(collectionResourceRel = "sport_results", path = "sport_results")
public interface SportResultRepository extends CrudRepository<SportResult, Long> {

	@PostFilter("hasRole('ROLE_ADMIN') or @sportResultPermissionEvaluator.hasPermission(authentication,filterObject,'read')")
	List<SportResult> findAll();

	@PreAuthorize("hasRole('ROLE_ADMIN') or @studentPermissionEvaluator.hasPermission(authentication,#student,'read')")
	List<SportResult> findByStudent(Student student);

	@PostAuthorize("hasRole('ROLE_ADMIN') or @sportResultPermissionEvaluator.hasPermission(authentication,returnObject.orElse(null),'read')")
	Optional<SportResult> findById(Long id);

	@PostFilter("hasRole('ROLE_ADMIN') or @sportResultPermissionEvaluator.hasPermission(authentication,filterObject,'read')")
	List<SportResult> findByDiscipline(DisciplineType discipline);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or @sportResultPermissionEvaluator.hasPermission(authentication,#entity,'write')")
	<S extends SportResult> S save(S entity);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or @sportResultPermissionEvaluator.hasPermission(authentication,#entity,'write')")
	void delete(SportResult entity);

}
