package com.bjs.bjsapi.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.repository.ClassRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;
import com.bjs.bjsapi.security.evaluators.StudentPermissionEvaluator;

@SpringJUnitConfig
public class TopStudentsControllerTest {

	@MockBean
	private StudentRepository studentRepository;

	@MockBean
	private StudentCalculationService studentRestService;

	@MockBean
	private ClassRepository classRepository;

	@MockBean
	private TopStudentsController topStudentsController;

	@MockBean
	private StudentPermissionEvaluator permissionEvaluator;

	@MockBean
	private EntityLinks entityLinks;

	private Student class7B_student2;
	private Student class7B_student1;
	private Student class7A_student1;
	private Student class7A_student5;
	private Student class7A_student3;
	private Student class7B_student5;

	@BeforeEach
	void setUp() {
		topStudentsController = new TopStudentsController(studentRestService, studentRepository, classRepository, entityLinks, permissionEvaluator);
		when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
		setupScenario();
	}

	@Test
	public void test_getTopMaleStudents() {
		// WHEN

		List<Student> students = topStudentsController.topStudents("7", false);

		// THEN / ASSERT

		assertThat(students.get(0)).isEqualTo(class7B_student2);
		assertThat(students.get(1)).isEqualTo(class7B_student1);
		assertThat(students.get(2)).isEqualTo(class7A_student1);
	}

	@Test
	public void test_getTopFemaleStudents() {
		// WHEN

		List<Student> students = topStudentsController.topStudents("7", true);

		// THEN / ASSERT

		assertThat(students.get(0)).isEqualTo(class7A_student5);
		assertThat(students.get(1)).isEqualTo(class7A_student3);
		assertThat(students.get(2)).isEqualTo(class7B_student5);
	}

	private void setupScenario() {
		//GIVEN
		Class class7A = Class.builder().grade("7").className("A").build();

		class7A_student1 = Student.builder()
			.firstName("Liam")
			.lastName("Heß")
			.female(false)
			.schoolClass(class7A)
			.build();
		Student class7A_student2 = Student.builder()
			.firstName("Ayk")
			.lastName("Borstelmann")
			.female(false)
			.schoolClass(class7A)
			.build();
		class7A_student3 = Student.builder()
			.firstName("Lucy")
			.lastName("Schmitz")
			.female(true)
			.schoolClass(class7A)
			.build();
		Student class7A_student4 = Student.builder()
			.firstName("Domenic")
			.lastName("Becker")
			.female(false)
			.schoolClass(class7A)
			.build();
		class7A_student5 = Student.builder()
			.firstName("Lisa")
			.lastName("Pahlings")
			.female(true)
			.schoolClass(class7A)
			.build();

		Class class7B = Class.builder().grade("7").className("B").build();

		class7B_student1 = Student.builder()
			.firstName("Marius")
			.lastName("Runkel")
			.female(false)
			.schoolClass(class7B)
			.build();
		class7B_student2 = Student.builder()
			.firstName("Patrick")
			.lastName("Salz")
			.female(false)
			.schoolClass(class7B)
			.build();
		Student class7B_student3 = Student.builder()
			.firstName("Jalen")
			.lastName("Buscemi")
			.female(false)
			.schoolClass(class7B)
			.build();
		Student class7B_student4 = Student.builder()
			.firstName("Tim")
			.lastName("Schmitt")
			.female(false)
			.schoolClass(class7B)
			.build();
		class7B_student5 = Student.builder()
			.firstName("Johanna")
			.lastName("Müller")
			.female(true)
			.schoolClass(class7B)
			.build();

		doReturn(1024).when(studentRestService).calculateScore(class7A_student1);
		doReturn(921).when(studentRestService).calculateScore(class7A_student2);
		doReturn(323).when(studentRestService).calculateScore(class7A_student3);
		doReturn(473).when(studentRestService).calculateScore(class7A_student4);
		doReturn(740).when(studentRestService).calculateScore(class7A_student5);
		doReturn(1362).when(studentRestService).calculateScore(class7B_student1);
		doReturn(1420).when(studentRestService).calculateScore(class7B_student2);
		doReturn(232).when(studentRestService).calculateScore(class7B_student3);
		doReturn(0).when(studentRestService).calculateScore(class7B_student4);
		doReturn(232).when(studentRestService).calculateScore(class7B_student5);

		doReturn(Arrays.asList(class7A, class7B)).when(classRepository).findByGrade("7");
		doReturn(Arrays.asList(class7A_student1, class7A_student2, class7A_student3, class7A_student4, class7A_student5)).when(studentRepository).findAllBySchoolClass(class7A);
		doReturn(Arrays.asList(class7B_student1, class7B_student2, class7B_student3, class7B_student4, class7B_student5)).when(studentRepository).findAllBySchoolClass(class7B);
	}

}
