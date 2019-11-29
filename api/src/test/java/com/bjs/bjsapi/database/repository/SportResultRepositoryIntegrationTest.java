package com.bjs.bjsapi.database.repository;

import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.enums.DisciplineType;
import com.bjs.bjsapi.database.model.helper.ClassBuilder;
import com.bjs.bjsapi.database.model.helper.SportResultBuilder;
import com.bjs.bjsapi.database.model.helper.StudentBuilder;
import com.bjs.bjsapi.database.model.helper.UserPrivilegeBuilder;

class SportResultRepositoryIntegrationTest extends RepositoryIntegrationTest {

	private Student accessibleStudent;
	private Student inaccessibleStudent;
	private SportResult accessibleStudentsResult;
	private SportResult inaccessibleStudentsResult;

	private List<FieldDescriptor> sportResultRequest = Arrays.asList(
		fieldWithPath("result").description("Result which the student achieved (in standard units)"),
		fieldWithPath("discipline").description("Discipline in which this measurement was recorded"),
		fieldWithPath("student").description("The student (as URI) this result belongs to")
	);
	private List<FieldDescriptor> sportResultResponse = Arrays.asList(
		fieldWithPath("result").description("Result which the student achieved (in standard units)"),
		fieldWithPath("discipline").description("Discipline in which this measurement was recorded"),
		subsectionWithPath("_links").description("All links regarding this result"),
		fieldWithPath("_links.student").description("URI to student this result belongs to")
	);

	@Override
	@BeforeEach
	void setUp() throws Exception {
		super.setUp();
		setupSportResultScenario();
	}

	@Override
	@AfterEach
	void tearDown() {
		super.tearDown();
	}

	@Test
	void test_create_unauthenticated() throws Exception {
		mvc.perform(post("/api/v1/sport_results")
			.content(givenNewSportResult(accessibleStudent.getId()))
			.with(anonymous())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_create_unauthorized() throws Exception {
		mvc.perform(post("/api/v1/sport_results")
			.content(givenNewSportResult(inaccessibleStudent.getId()))
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_create_authorized() throws Exception {
		mvc.perform(post("/api/v1/sport_results")
			.content(givenNewSportResult(accessibleStudent.getId()))
			.contentType(MediaType.APPLICATION_JSON)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("result").value(6.6))
			.andExpect(jsonPath("discipline").value("RUN_50"))
			.andDo(document("sport-results-create",
				requestFields(sportResultRequest),
				responseFields(sportResultResponse)
			));
	}

	@Test
	void test_create_admin() throws Exception {
		mvc.perform(post("/api/v1/sport_results")
			.content(givenNewSportResult(accessibleStudent.getId()))
			.contentType(MediaType.APPLICATION_JSON)
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("result").value(6.6))
			.andExpect(jsonPath("discipline").value("RUN_50"));

		mvc.perform(post("/api/v1/sport_results")
			.content(givenNewSportResult(inaccessibleStudent.getId()))
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("result").value(6.6))
			.andExpect(jsonPath("discipline").value("RUN_50"));
	}

	private String givenNewSportResult(Long studentID) {
		//language=JSON
		String sportResult = "{\n" +
			"  \"result\":6.6,\n" +
			"  \"discipline\":\"RUN_50\",\n" +
			"  \"student\":\"/%d\"\n" +
			"}";

		return String.format(sportResult, studentID);
	}

	void setupSportResultScenario() {
		runAsAdmin(() -> {
			Class accessibleClass = classRepository.save(new ClassBuilder().setClassName("7A").setClassTeacherName("Teacher").createClass());
			Class inaccessibleClass = classRepository.save(new ClassBuilder().setClassName("7B").setClassTeacherName("Teacher").createClass());

			accessibleStudent = studentRepository.save(new StudentBuilder().setFirstName("First").setLastName("Student").setFemale(false).setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28))).setSchoolClass(accessibleClass).createStudent());
			inaccessibleStudent = studentRepository.save(new StudentBuilder().setFirstName("First").setLastName("Student").setFemale(false).setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28))).setSchoolClass(inaccessibleClass).createStudent());

			accessibleStudentsResult = sportResultRepository.save(new SportResultBuilder().setStudent(accessibleStudent).setDiscipline(DisciplineType.RUN_50).setResult(6.6F).createSportResult());
			inaccessibleStudentsResult = sportResultRepository.save(new SportResultBuilder().setStudent(inaccessibleStudent).setDiscipline(DisciplineType.RUN_50).setResult(6.6F).createSportResult());

			userPrivilegeRepository.save(new UserPrivilegeBuilder().setAccessibleClass(accessibleClass).setUser(user).createUserPrivilege());
		});
	}

}