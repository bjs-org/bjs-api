package com.bjs.bjsapi.controllers;

import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.enums.DisciplineType;
import com.bjs.bjsapi.database.model.enums.StudentPaper;
import com.bjs.bjsapi.database.repository.SportResultRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;
import com.bjs.bjsapi.helper.CalculationInformationService;
import com.bjs.bjsapi.helper.ClassificationInformationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.sql.Date;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@SpringJUnitConfig
class StudentRestControllerTest {

	@MockBean
	private ClassificationInformationService classificationInformationService;

	@MockBean
	private CalculationInformationService calculationInformationService;

	@MockBean
	private SportResultRepository sportResultRepository;

	@MockBean
	private StudentRepository studentRepository;

	@MockBean
	private Clock clock;

	private StudentRestController studentRestController;

	@BeforeEach
	void setUp() {
		studentRestController = new StudentRestController(studentRepository, sportResultRepository, calculationInformationService, classificationInformationService, clock);
	}

	@Test
	void test_calculation_run_50() {
		Student student = new Student();
		student.setFemale(true);
		SportResult sportResult = new SportResult();

		sportResult.setDiscipline(DisciplineType.RUN_50);
		sportResult.setResult(7.00F);

		doReturn(Collections.singletonList(sportResult)).when(sportResultRepository).findByStudent(student);
		doReturn(3.79000).when(calculationInformationService).getAValue(true, DisciplineType.RUN_50);
		doReturn(0.00690).when(calculationInformationService).getCValue(true, DisciplineType.RUN_50);

		Integer integer = studentRestController.calculateScore(student);

		assertThat(integer).isEqualTo(451);
	}

	@Test
	void test_calculation_run_75() {
		Student student = new Student();
		student.setFemale(true);
		SportResult sportResult = new SportResult();

		sportResult.setDiscipline(DisciplineType.RUN_75);
		sportResult.setResult(9.00F);

		doReturn(Collections.singletonList(sportResult)).when(sportResultRepository).findByStudent(student);
		doReturn(4.10000).when(calculationInformationService).getAValue(true, DisciplineType.RUN_75);
		doReturn(0.00664).when(calculationInformationService).getCValue(true, DisciplineType.RUN_75);

		Integer integer = studentRestController.calculateScore(student);

		assertThat(integer).isEqualTo(604);
	}

	@Test
	void test_calculation_run_100() {
		Student student = new Student();
		student.setFemale(true);
		SportResult sportResult = new SportResult();

		sportResult.setDiscipline(DisciplineType.RUN_100);
		sportResult.setResult(12.00F);

		doReturn(Collections.singletonList(sportResult)).when(sportResultRepository).findByStudent(student);
		doReturn(4.341).when(calculationInformationService).getAValue(true, DisciplineType.RUN_100);
		doReturn(0.00676).when(calculationInformationService).getCValue(true, DisciplineType.RUN_100);

		Integer integer = studentRestController.calculateScore(student);

		assertThat(integer).isEqualTo(566);
	}

	@Test
	void test_calculation_high_jump() {
		Student student = new Student();
		student.setFemale(true);
		SportResult sportResult = new SportResult();

		sportResult.setDiscipline(DisciplineType.HIGH_JUMP);
		sportResult.setResult(1.50F);

		doReturn(Collections.singletonList(sportResult)).when(sportResultRepository).findByStudent(student);
		doReturn(0.841).when(calculationInformationService).getAValue(true, DisciplineType.HIGH_JUMP);
		doReturn(0.00080).when(calculationInformationService).getCValue(true, DisciplineType.HIGH_JUMP);

		Integer integer = studentRestController.calculateScore(student);

		assertThat(integer).isEqualTo(479);
	}

	@Test
	void test_calculation_general() {
		Student student = new Student();
		student.setFemale(false);

		SportResult resultRUN_100 = new SportResult();
		resultRUN_100.setDiscipline(DisciplineType.RUN_100);
		resultRUN_100.setResult(12.00F);

		SportResult resultRUN_800 = new SportResult();
		resultRUN_800.setDiscipline(DisciplineType.RUN_800);
		resultRUN_800.setResult(210.00F);

		SportResult resultJUMP = new SportResult();
		resultJUMP.setDiscipline(DisciplineType.LONG_JUMP);
		resultJUMP.setResult(4.20F);

		SportResult resultTHROW_200 = new SportResult();
		resultTHROW_200.setDiscipline(DisciplineType.BALL_THROWING_200);
		resultTHROW_200.setResult(35.00F);

		doReturn(Arrays.asList(resultJUMP, resultRUN_100, resultRUN_800, resultTHROW_200)).when(sportResultRepository).findByStudent(student);

		doReturn(4.00620).when(calculationInformationService).getAValue(false, DisciplineType.RUN_100);
		doReturn(2.02320).when(calculationInformationService).getAValue(false, DisciplineType.RUN_800);
		doReturn(1.09350).when(calculationInformationService).getAValue(false, DisciplineType.LONG_JUMP);
		doReturn(1.41490).when(calculationInformationService).getAValue(false, DisciplineType.BALL_THROWING_200);

		doReturn(0.00656).when(calculationInformationService).getCValue(false, DisciplineType.RUN_100);
		doReturn(0.00647).when(calculationInformationService).getCValue(false, DisciplineType.RUN_800);
		doReturn(0.00208).when(calculationInformationService).getCValue(false, DisciplineType.LONG_JUMP);
		doReturn(0.01039).when(calculationInformationService).getCValue(false, DisciplineType.BALL_THROWING_200);

		Integer integer = studentRestController.calculateScore(student);

		assertThat(integer).isEqualTo(1526);
	}

	@Test
	public void test_classification() {
		Student student = new Student();
		Date studentsBirthday = Date.valueOf(LocalDate.of(2000, 1, 1));
		student.setFemale(false);
		student.setBirthDay(studentsBirthday);

		studentRestController = spy(studentRestController);
		doReturn(1526).when(studentRestController).calculateScore(student);
		doReturn(Instant.parse("2019-01-01T00:00:00.00Z")).when(clock).instant();

		doReturn(1275).when(classificationInformationService).getVictoryValue(false, 19);
		doReturn(1550).when(classificationInformationService).getHonorValue(false, 19);

		assertThat(studentRestController.classification(student)).isEqualTo(StudentPaper.VICTORY);
	}

	@Test
	public void testBirthday() {
		Student student = new Student();
		Date studentsBirthday = Date.valueOf(LocalDate.of(2000, 3, 1));
		student.setFemale(false);
		student.setBirthDay(studentsBirthday);

		Integer integer = student.getStudentAge(Clock.fixed(Instant.parse("2019-01-01T00:00:00.00Z"), ZoneId.systemDefault()));
		assertThat(integer).isEqualTo(19);
	}

}