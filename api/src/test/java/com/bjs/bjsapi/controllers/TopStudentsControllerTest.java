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
import com.bjs.bjsapi.database.model.helper.ClassBuilder;
import com.bjs.bjsapi.database.model.helper.StudentBuilder;
import com.bjs.bjsapi.database.repository.ClassRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;
import com.bjs.bjsapi.security.evaluators.StudentPermissionEvaluator;

@SpringJUnitConfig
public class TopStudentsControllerTest {

	@MockBean
	private StudentRepository studentRepository;

	@MockBean
	private StudentRestController studentRestController;

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
		topStudentsController = new TopStudentsController(studentRestController, studentRepository, classRepository, entityLinks, permissionEvaluator);
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
		Class class7A = new ClassBuilder().setGrade("7").setClassName("A").createClass();

		class7A_student1 = new StudentBuilder()
			.setFirstName("Liam")
			.setLastName("Heß")
			.setFemale(false)
			.setSchoolClass(class7A)
			.createStudent();
		Student class7A_student2 = new StudentBuilder()
			.setFirstName("Ayk")
			.setLastName("Borstelmann")
			.setFemale(false)
			.setSchoolClass(class7A)
			.createStudent();
		class7A_student3 = new StudentBuilder()
			.setFirstName("Lucy")
			.setLastName("Schmitz")
			.setFemale(true)
			.setSchoolClass(class7A)
			.createStudent();
		Student class7A_student4 = new StudentBuilder()
			.setFirstName("Domenic")
			.setLastName("Becker")
			.setFemale(false)
			.setSchoolClass(class7A)
			.createStudent();
		class7A_student5 = new StudentBuilder()
			.setFirstName("Lisa")
			.setLastName("Pahlings")
			.setFemale(true)
			.setSchoolClass(class7A)
			.createStudent();

		Class class7B = new ClassBuilder().setGrade("7").setClassName("A").createClass();

		class7B_student1 = new StudentBuilder()
			.setFirstName("Marius")
			.setLastName("Runkel")
			.setFemale(false)
			.setSchoolClass(class7B)
			.createStudent();
		class7B_student2 = new StudentBuilder()
			.setFirstName("Patrick")
			.setLastName("Salz")
			.setFemale(false)
			.setSchoolClass(class7B)
			.createStudent();
		Student class7B_student3 = new StudentBuilder()
			.setFirstName("Jalen")
			.setLastName("Buscemi")
			.setFemale(false)
			.setSchoolClass(class7B)
			.createStudent();
		Student class7B_student4 = new StudentBuilder()
			.setFirstName("Tim")
			.setLastName("Schmitt")
			.setFemale(false)
			.setSchoolClass(class7B)
			.createStudent();
		class7B_student5 = new StudentBuilder()
			.setFirstName("Johanna")
			.setLastName("Müller")
			.setFemale(true)
			.setSchoolClass(class7B)
			.createStudent();

		doReturn(1024).when(studentRestController).calculateScore(class7A_student1);
		doReturn(921).when(studentRestController).calculateScore(class7A_student2);
		doReturn(323).when(studentRestController).calculateScore(class7A_student3);
		doReturn(473).when(studentRestController).calculateScore(class7A_student4);
		doReturn(740).when(studentRestController).calculateScore(class7A_student5);
		doReturn(1362).when(studentRestController).calculateScore(class7B_student1);
		doReturn(1420).when(studentRestController).calculateScore(class7B_student2);
		doReturn(232).when(studentRestController).calculateScore(class7B_student3);
		doReturn(0).when(studentRestController).calculateScore(class7B_student4);
		doReturn(232).when(studentRestController).calculateScore(class7B_student5);

		doReturn(Arrays.asList(class7A, class7B)).when(classRepository).findByGrade("7");
		doReturn(Arrays.asList(class7A_student1, class7A_student2, class7A_student3, class7A_student4, class7A_student5)).when(studentRepository).findAllBySchoolClass(class7A);
		doReturn(Arrays.asList(class7B_student1, class7B_student2, class7B_student3, class7B_student4, class7B_student5)).when(studentRepository).findAllBySchoolClass(class7B);
	}

}
