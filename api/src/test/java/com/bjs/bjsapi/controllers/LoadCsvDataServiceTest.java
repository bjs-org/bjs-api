package com.bjs.bjsapi.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.repository.ClassRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;

@SpringJUnitConfig
class LoadCsvDataServiceTest {

	@MockBean
	public ClassRepository classRepository;

	@MockBean
	public StudentRepository studentRepository;

	public LoadCsvDataService loadCsvDataService;

	@BeforeEach
	void setUp() {
		loadCsvDataService = new LoadCsvDataService(studentRepository, classRepository);
	}

	@Test
	void test_empty() {
		loadCsvDataService.loadCsv(Collections.emptyList());
	}

	@Test
	void test_wrongFormat() throws IOException {
		final Path path = Paths.get("src/test/resources/sample_student_csv_wrong_format.csv");
		final List<String> strings = Files.readAllLines(path);

		assertThatThrownBy(() -> loadCsvDataService.loadCsv(strings))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void test_wrongFormat_noClassName() throws IOException {
		final Path path = Paths.get("src/test/resources/sample_student_csv_no_class_name.csv");
		final List<String> strings = Files.readAllLines(path);

		when(classRepository.save(any())).then(invocationOnMock -> invocationOnMock.getArgument(0));
		when(studentRepository.save(any())).then(invocationOnMock -> invocationOnMock.getArgument(0));

		loadCsvDataService.loadCsv(strings);

		ArgumentCaptor<Class> classArgumentCaptor = ArgumentCaptor.forClass(Class.class);

		verify(classRepository, atLeastOnce()).save(classArgumentCaptor.capture());

		final Class capturedClass = classArgumentCaptor.getValue();

		assertThat(capturedClass.getClassName()).isEqualTo("");
		assertThat(capturedClass.getGrade()).isEqualTo("EF");
	}

	@Test
	void test_wrongFormat_multipleClasses() throws IOException {
		final Path path = Paths.get("src/test/resources/sample_student_csv_multiple_classes.csv");
		final List<String> strings = Files.readAllLines(path);

		when(classRepository.save(any())).then(invocationOnMock -> invocationOnMock.getArgument(0));
		when(studentRepository.save(any())).then(invocationOnMock -> invocationOnMock.getArgument(0));

		loadCsvDataService.loadCsv(strings);

		ArgumentCaptor<Class> classArgumentCaptor = ArgumentCaptor.forClass(Class.class);

		verify(classRepository, atLeastOnce()).save(classArgumentCaptor.capture());

		final List<Class> allValues = classArgumentCaptor.getAllValues();
		assertThat(allValues)
			.anySatisfy(schoolClass -> {
				assertThat(schoolClass.getGrade()).isEqualTo("8");
				assertThat(schoolClass.getGrade()).isEqualTo("a");
			})
			.anySatisfy(schoolClass -> {
				assertThat(schoolClass.getGrade()).isEqualTo("8");
				assertThat(schoolClass.getGrade()).isEqualTo("b");
			});
	}

	@Test
	void test_loadCsv() throws IOException {
		final Path path = Paths.get("src/test/resources/sample_student_csv.csv");
		final List<String> strings = Files.readAllLines(path);

		when(classRepository.save(any())).then(invocationOnMock -> invocationOnMock.getArgument(0));
		when(studentRepository.save(any())).then(invocationOnMock -> invocationOnMock.getArgument(0));

		loadCsvDataService.loadCsv(strings);

		ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
		ArgumentCaptor<Class> classArgumentCaptor = ArgumentCaptor.forClass(Class.class);

		verify(classRepository, atLeastOnce()).save(classArgumentCaptor.capture());
		verify(studentRepository, atLeastOnce()).save(studentArgumentCaptor.capture());

		final Class capturedClass = classArgumentCaptor.getValue();

		assertThat(capturedClass.getClassName()).isEqualTo("a");
		assertThat(capturedClass.getGrade()).isEqualTo("8");

		final List<Student> allValues = studentArgumentCaptor.getAllValues();
		assertThat(allValues).hasSize(3);

		assertThat(allValues)
			.anySatisfy(student -> {
				assertThat(student.getFirstName()).isEqualTo("Liam Sky");
				assertThat(student.getLastName()).isEqualTo("Heß");
				assertThat(student.getBirthDay()).isEqualTo("2001-08-15");
				assertThat(student.getFemale()).isFalse();
				assertThat(student.getSchoolClass()).isEqualTo(capturedClass);
			})
			.anySatisfy(student -> {
				assertThat(student.getFirstName()).isEqualTo("Jalen");
				assertThat(student.getLastName()).isEqualTo("Buscemi");
				assertThat(student.getBirthDay()).isEqualTo("2001-11-24");
				assertThat(student.getFemale()).isFalse();
				assertThat(student.getSchoolClass()).isEqualTo(capturedClass);
			})
			.anySatisfy(student -> {
				assertThat(student.getFirstName()).isEqualTo("Ayk");
				assertThat(student.getLastName()).isEqualTo("Borstelmann");
				assertThat(student.getBirthDay()).isEqualTo("2002-03-28");
				assertThat(student.getFemale()).isFalse();
				assertThat(student.getSchoolClass()).isEqualTo(capturedClass);
			});
	}

}