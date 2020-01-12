package com.bjs.bjsapi.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Clock;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import com.bjs.bjsapi.config.ApiConfiguration;
import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.enums.DisciplineType;
import com.bjs.bjsapi.database.repository.SportResultRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;
import com.bjs.bjsapi.helper.CalculationInformationService;
import com.bjs.bjsapi.helper.ClassificationInformationService;
import com.bjs.bjsapi.security.BJSUserDetailsService;

@SpringJUnitConfig
@WebMvcTest(controllers = StudentRestController.class)
class StudentRestControllerIntegrationTest {

	@MockBean
	private ClassificationInformationService classificationInformationService;

	@MockBean
	private Clock clock;

	@MockBean
	private BJSUserDetailsService bjsUserDetailsService;

	@MockBean
	private StudentRepository studentRepository;

	@MockBean
	private SportResultRepository sportResultRepository;

	@MockBean
	private CalculationInformationService calculationInformationService;

	@MockBean
	public ApiConfiguration apiConfiguration;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser("admin")
	void test_calculateScore_found() throws Exception {
		Student student = new Student();
		student.setFemale(true);
		SportResult sportResult = new SportResult();

		sportResult.setDiscipline(DisciplineType.RUN_50);
		sportResult.setResult(7.00F);

		doReturn(Collections.singletonList(sportResult)).when(sportResultRepository).findByStudent(student);
		doReturn(Optional.of(student)).when(studentRepository).findById(1L);
		doReturn(3.79000).when(calculationInformationService).getAValue(true, DisciplineType.RUN_50);
		doReturn(0.00690).when(calculationInformationService).getCValue(true, DisciplineType.RUN_50);

		mockMvc.perform(get("/api/v1/students/{id}/score", 1)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string(String.valueOf(451)));
	}

	@Test
	@WithMockUser("admin")
	void test_calculateScore_notFound() throws Exception {
		Student student = new Student();
		student.setFemale(true);
		SportResult sportResult = new SportResult();

		sportResult.setDiscipline(DisciplineType.RUN_50);
		sportResult.setResult(7.00F);

		doReturn(Collections.singletonList(sportResult)).when(sportResultRepository).findByStudent(student);
		doReturn(Optional.of(student)).when(studentRepository).findById(1L);
		doReturn(3.79000).when(calculationInformationService).getAValue(true, DisciplineType.RUN_50);
		doReturn(0.00690).when(calculationInformationService).getCValue(true, DisciplineType.RUN_50);

		mockMvc.perform(get("/api/v1/students/{id}/score", 2)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

}