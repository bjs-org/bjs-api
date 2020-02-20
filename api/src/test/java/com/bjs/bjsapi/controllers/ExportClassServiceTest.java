package com.bjs.bjsapi.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.enums.DisciplineType;
import com.bjs.bjsapi.database.model.enums.StudentPaper;
import com.bjs.bjsapi.database.model.helper.ClassBuilder;
import com.bjs.bjsapi.database.model.helper.SportResultBuilder;
import com.bjs.bjsapi.database.model.helper.StudentBuilder;
import com.bjs.bjsapi.database.repository.ClassRepository;

@SpringJUnitConfig
class ExportClassServiceTest {

	@MockBean
	private ClassRepository classRepository;
	@MockBean
	private StudentCalculationService studentCalculationService;

	private ExportClassService exportClassService;

	@BeforeEach
	void setUp() {
		exportClassService = new ExportClassService(classRepository, studentCalculationService);
	}

	@Test
	void export_class() {

		Class schoolClass = new ClassBuilder()
			.setClassName("A")
			.setGrade("7")
			.setClassTeacherName("Gutsche")
			.createClass();

		Student student = new StudentBuilder()
			.setFirstName("Liam")
			.setLastName("Heß")
			.setBirthDay(Date.valueOf("2002-03-28"))
			.setFemale(false)
			.setSchoolClass(schoolClass)
			.createStudent();

		SportResult firstSportResult = new SportResultBuilder()
			.setResult(5.5F)
			.setDiscipline(DisciplineType.RUN_50)
			.setStudent(student)
			.createSportResult();

		SportResult secondSportResult = new SportResultBuilder()
			.setResult(44.5F)
			.setDiscipline(DisciplineType.BALL_THROWING_80)
			.setStudent(student)
			.createSportResult();

		SportResult thirdSportResult = new SportResultBuilder()
			.setResult(3.3F)
			.setDiscipline(DisciplineType.LONG_JUMP)
			.setStudent(student)
			.createSportResult();

		student.setSportResults(Arrays.asList(firstSportResult, secondSportResult, thirdSportResult));
		schoolClass.setStudents(Collections.singletonList(student));

		when(classRepository.findById(anyLong())).thenReturn(Optional.of(schoolClass));
		when(studentCalculationService.calculateScore(student)).thenReturn(1400);
		when(studentCalculationService.classifyScore(student)).thenReturn(StudentPaper.HONOR);

		final List<String> strings = exportClassService.exportClass(1L);
		assertThat(strings.get(3)).isEqualToIgnoringWhitespace("Liam Heß;m;1400;HONOR;5,5;3,3;44,5;");
	}

}