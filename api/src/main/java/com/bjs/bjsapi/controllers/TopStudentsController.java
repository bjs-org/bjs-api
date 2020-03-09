package com.bjs.bjsapi.controllers;

import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjs.bjsapi.controllers.responses.MaleFemaleResponse;
import com.bjs.bjsapi.controllers.responses.TopResponse;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.repository.ClassRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;
import com.bjs.bjsapi.security.evaluators.StudentPermissionEvaluator;

@RepositoryRestController
@RequestMapping("/students/best/")
public class TopStudentsController {

	private final StudentCalculationService studentCalculationService;
	private final StudentRepository studentRepository;
	private final ClassRepository classRepository;
	private final EntityLinks entityLinks;
	private final StudentPermissionEvaluator studentPermissionEvaluator;

	public TopStudentsController(StudentCalculationService studentCalculationService, StudentRepository studentRepository, ClassRepository classRepository, EntityLinks entityLinks, StudentPermissionEvaluator studentPermissionEvaluator) {
		this.studentCalculationService = studentCalculationService;
		this.studentRepository = studentRepository;
		this.classRepository = classRepository;
		this.entityLinks = entityLinks;
		this.studentPermissionEvaluator = studentPermissionEvaluator;
	}

	@RequestMapping(value = "/{grade}", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
	public @ResponseBody
	RepresentationModel<?> topStudentsMapping(@PathVariable String grade, @RequestParam(required = false) Boolean female, PersistentEntityResourceAssembler resourceAssembler, Authentication authentication) {
		RepresentationModel<?> body;

		if (female == null) {
			MaleFemaleResponse<EntityModel<?>> response = new MaleFemaleResponse<>();

			response.setFemale(createEntityModel(grade, true, resourceAssembler, authentication));
			response.setMale(createEntityModel(grade, false, resourceAssembler, authentication));

			body = new EntityModel<>(response);
		} else {
			body = constructResponse(topStudents(grade, female), resourceAssembler, authentication);
		}

		body.add(entityLinks.linkToCollectionResource(Student.class));
		body.add(selfLink(grade, female));
		body.add(selfLink(grade, null).withRel("best"));

		return body;
	}

	private EntityModel<?> createEntityModel(String grade, boolean female, PersistentEntityResourceAssembler resourceAssembler, Authentication authentication) {
		final List<Student> topStudents = topStudents(grade, female);

		final EntityModel<?> topMaleStudents = constructResponse(topStudents, resourceAssembler, authentication);
		topMaleStudents.add(selfLink(grade, female));

		return topMaleStudents;
	}

	private Link selfLink(@PathVariable String grade, @RequestParam(required = false) Boolean female) {
		return linkTo(methodOn(TopStudentsController.class).topStudentsMapping(grade, female, null, null)).withSelfRel();
	}

	private EntityModel<TopResponse<?>> constructResponse(List<Student> topStudents, PersistentEntityResourceAssembler resourceAssembler, Authentication authentication) {
		return new EntityModel<>(TopResponse.of(topStudents, s -> convertToMockIfNotAccessible(resourceAssembler, authentication, s)));
	}

	private RepresentationModel<?> convertToMockIfNotAccessible(PersistentEntityResourceAssembler resourceAssembler, Authentication authentication, Student student) {
		final boolean hasPermission = AuthorityUtils.authorityListToSet(authentication.getAuthorities()).contains("ROLE_ADMIN") || studentPermissionEvaluator.hasPermission(authentication, student, "read");
		if (hasPermission) {
			return resourceAssembler.toModel(student);
		} else {
			return new RepresentationModel<>(entityLinks.linkToItemResource(Student.class, student.getId()));
		}
	}

	public List<Student> topStudents(String grade, boolean female) {
		return runAsAdmin(() -> classRepository.findByGrade(grade)
			.stream()
			.map(studentRepository::findAllBySchoolClass)
			.flatMap(Collection::stream)
			.filter(student -> student.getFemale() == female)
			.sorted(Collections.reverseOrder(Comparator.comparingInt(studentCalculationService::calculateScore)))
			.limit(3)
			.collect(Collectors.toList())
		);
	}

}
