package com.bjs.bjsapi.database.repository;

import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
import org.springframework.restdocs.request.ParameterDescriptor;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.enums.DisciplineType;
import com.bjs.bjsapi.database.model.helper.ClassBuilder;
import com.bjs.bjsapi.database.model.helper.SportResultBuilder;
import com.bjs.bjsapi.database.model.helper.StudentBuilder;
import com.bjs.bjsapi.database.model.helper.UserPrivilegeBuilder;

class SportResultRepositoryIntegrationTest extends RepositoryIntegrationTest {

	private final ParameterDescriptor idDescriptor = parameterWithName("id").description("The sport-result's id");
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
	private List<FieldDescriptor> sportResultsResponse = Arrays.asList(
		subsectionWithPath("_embedded.sport_results").description("Array of all results the user has access to"),
		subsectionWithPath("_links").description("All links regarding this repository")
	);
	private final ParameterDescriptor studentDescriptor = parameterWithName("student").description("URI to student this result belongs to");
	private ParameterDescriptor disciplineDescriptor = parameterWithName("discipline").description("Discipline in which this measurement was recorded");

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
			.andExpect(jsonPath("discipline").value("RUN_100"))
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
			.andExpect(jsonPath("discipline").value("RUN_100"));

		mvc.perform(post("/api/v1/sport_results")
			.content(givenNewSportResult(inaccessibleStudent.getId()))
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("result").value(6.6))
			.andExpect(jsonPath("discipline").value("RUN_100"));
	}

	@Test
	void test_findAll_unauthenticated() throws Exception {
		mvc.perform(get("/api/v1/sport_results")
			.with(anonymous())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_findAll_authorized() throws Exception {
		mvc.perform(get("/api/v1/sport_results")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("_embedded.sport_results", hasSize(1)))
			.andExpect(jsonPath("_embedded.sport_results[*].result", hasItem(6.6)))
			.andExpect(jsonPath("_embedded.sport_results[*].discipline", hasItem(accessibleStudentsResult.getDiscipline().toString())))
			.andDo(document("sport-results-get-all",
				responseFields(sportResultsResponse).andWithPrefix("_embedded.sport_results[].", sportResultResponse)
			));
	}

	@Test
	void test_findAll_admin() throws Exception {
		mvc.perform(get("/api/v1/sport_results")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("_embedded.sport_results", hasSize(2)))
			.andExpect(jsonPath("_embedded.sport_results[*].result", hasItems(6.6, 6.6)))
			.andExpect(jsonPath("_embedded.sport_results[*].discipline", hasItems(accessibleStudentsResult.getDiscipline().toString(), inaccessibleStudentsResult.getDiscipline().toString())));
	}

	@Test
	void test_findByID_unauthenticated() throws Exception {
		mvc.perform(get("/api/v1/sport_results/{id}", accessibleStudentsResult.getId())
			.with(anonymous())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_findByID_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/sport_results/{id}", inaccessibleStudentsResult.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findByID_authorized() throws Exception {
		mvc.perform(get("/api/v1/sport_results/{id}", accessibleStudentsResult.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("result").value(6.6))
			.andExpect(jsonPath("discipline").value(accessibleStudentsResult.getDiscipline().toString()))
			.andDo(document("sport-result-get-byId",
				pathParameters(idDescriptor),
				responseFields(sportResultResponse)
			));
	}

	@Test
	void test_findByID_admin() throws Exception {
		mvc.perform(get("/api/v1/sport_results/{id}", accessibleStudentsResult.getId())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("result").value(6.6))
			.andExpect(jsonPath("discipline").value(accessibleStudentsResult.getDiscipline().toString()));

		mvc.perform(get("/api/v1/sport_results/{id}", inaccessibleStudentsResult.getId())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("result").value(6.6))
			.andExpect(jsonPath("discipline").value(inaccessibleStudentsResult.getDiscipline().toString()));
	}

	@Test
	void test_findByStudent_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/sport_results/search/findByStudent?student={student}", "/" + inaccessibleStudent.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findByStudent_authorized() throws Exception {
		mvc.perform(get("/api/v1/sport_results/search/findByStudent?student={student}", "/" + inaccessibleStudent.getId())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("_embedded.sport_results", hasSize(1)))
			.andExpect(jsonPath("_embedded.sport_results.[*].result", hasItem(6.6)))
			.andExpect(jsonPath("_embedded.sport_results.[*].discipline", hasItem(inaccessibleStudentsResult.getDiscipline().toString())))
			.andDo(document("sport-results-get-byStudent-authorized",
				requestParameters(studentDescriptor),
				responseFields(sportResultsResponse).andWithPrefix("_embedded.sport_results.[].", sportResultResponse)
			));
	}

	@Test
	void test_findByStudent_admin() throws Exception {
		mvc.perform(get("/api/v1/sport_results/search/findByStudent?student={student}", "/" + accessibleStudent.getId())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("_embedded.sport_results", hasSize(1)))
			.andExpect(jsonPath("_embedded.sport_results.[*].result", hasItem(6.6)))
			.andExpect(jsonPath("_embedded.sport_results.[*].discipline", hasItem(accessibleStudentsResult.getDiscipline().toString())));
	}

	@Test
	void test_findByDiscipline_authorized() throws Exception {
		mvc.perform(get("/api/v1/sport_results/search/findByDiscipline?discipline={discipline}", "RUN_50")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("_embedded.sport_results", hasSize(1)))
			.andExpect(jsonPath("_embedded.sport_results.[*].result", hasItem(6.6)))
			.andExpect(jsonPath("_embedded.sport_results.[*].discipline", hasItem(accessibleStudentsResult.getDiscipline().toString())))
			.andDo(document("sport-result-get-byDiscipline",
				requestParameters(disciplineDescriptor),
				responseFields(sportResultsResponse).andWithPrefix("_embedded.sport_results[].", sportResultResponse)
			));
	}

	@Test
	void test_findByDiscipline_admin() throws Exception {
		mvc.perform(get("/api/v1/sport_results/search/findByDiscipline?discipline={discipline}", "RUN_50")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("_embedded.sport_results", hasSize(2)))
			.andExpect(jsonPath("_embedded.sport_results.[*].result", hasItems(6.6, 6.6)))
			.andExpect(jsonPath("_embedded.sport_results.[*].discipline", hasItems(accessibleStudentsResult.getDiscipline().toString(), inaccessibleStudentsResult.getDiscipline().toString())));
	}

	@Test
	void test_edit_unauthenticated() throws Exception {
		mvc.perform(patch("/api/v1/sport_results/{id}", inaccessibleStudentsResult.getId())
			.content(givenNewSportResult(inaccessibleStudent.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.with(anonymous()))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_edit_unauthorized() throws Exception {
		mvc.perform(patch("/api/v1/sport_results/{id}", inaccessibleStudentsResult.getId())
			.content(givenNewSportResult(inaccessibleStudent.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_edit_unauthorized_accessibleStudent_inaccessibleResult() throws Exception {
		mvc.perform(patch("/api/v1/sport_results/{id}", inaccessibleStudentsResult.getId())
			.content(givenNewSportResult(accessibleStudent.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_edit_unauthorized_inaccessibleStudent_accessibleResult() throws Exception {
		mvc.perform(patch("/api/v1/sport_results/{id}", accessibleStudentsResult.getId())
			.content(givenNewSportResult(inaccessibleStudent.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_edit_authorized() throws Exception {
		mvc.perform(patch("/api/v1/sport_results/{id}", accessibleStudentsResult.getId())
			.content(givenNewSportResult(accessibleStudent.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("discipline").value("RUN_100"))
			.andDo(document("sport-results-patch",
				pathParameters(idDescriptor),
				requestFields(sportResultRequest),
				responseFields(sportResultResponse)
			));
	}

	@Test
	void test_edit_admin() throws Exception {
		mvc.perform(patch("/api/v1/sport_results/{id}", accessibleStudentsResult.getId())
			.content(givenNewSportResult(accessibleStudent.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("discipline").value("RUN_100"))
			.andDo(document("sport-results-patch",
				pathParameters(idDescriptor),
				requestFields(sportResultRequest),
				responseFields(sportResultResponse)
			));

		mvc.perform(patch("/api/v1/sport_results/{id}", inaccessibleStudentsResult.getId())
			.content(givenNewSportResult(inaccessibleStudent.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("discipline").value("RUN_100"));
	}

	@Test
	void test_replace_unauthorized() throws Exception {
		mvc.perform(put("/api/v1/sport_results/{id}", inaccessibleStudentsResult.getId())
			.content(givenNewSportResult(inaccessibleStudent.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_replace_unauthorized_accessibleStudent_inaccessibleResult() throws Exception {
		mvc.perform(put("/api/v1/sport_results/{id}", inaccessibleStudentsResult.getId())
			.content(givenNewSportResult(accessibleStudent.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_replace_unauthorized_inaccessibleStudent_accessibleResult() throws Exception {
		mvc.perform(put("/api/v1/sport_results/{id}", accessibleStudentsResult.getId())
			.content(givenNewSportResult(inaccessibleStudent.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_replace_authorized() throws Exception {
		mvc.perform(put("/api/v1/sport_results/{id}", accessibleStudentsResult.getId())
			.content(givenNewSportResult(accessibleStudent.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("discipline").value("RUN_100"))
			.andDo(document("sport-results-put",
				pathParameters(idDescriptor),
				requestFields(sportResultRequest),
				responseFields(sportResultResponse)
			));
	}

	@Test
	void test_replace_admin() throws Exception {
		mvc.perform(put("/api/v1/sport_results/{id}", accessibleStudentsResult.getId())
			.content(givenNewSportResult(accessibleStudent.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("discipline").value("RUN_100"));

		mvc.perform(put("/api/v1/sport_results/{id}", inaccessibleStudentsResult.getId())
			.content(givenNewSportResult(inaccessibleStudent.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("discipline").value("RUN_100"));
	}

	@Test
	void test_delete_unauthenticated() throws Exception {
		mvc.perform(delete("/api/v1/sport_results/{id}", accessibleStudentsResult.getId())
			.with(anonymous()))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_delete_unauthorized() throws Exception {
		mvc.perform(delete("/api/v1/sport_results/{id}", inaccessibleStudentsResult.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_delete_authorized() throws Exception {
		mvc.perform(delete("/api/v1/sport_results/{id}", accessibleStudentsResult.getId())
			.with(asUser()))
			.andExpect(status().isNoContent());
	}

	@Test
	void test_delete_admin() throws Exception {
		mvc.perform(delete("/api/v1/sport_results/{id}", accessibleStudentsResult.getId())
			.with(asAdmin()))
			.andExpect(status().isNoContent());

		mvc.perform(delete("/api/v1/sport_results/{id}", inaccessibleStudentsResult.getId())
			.with(asAdmin()))
			.andExpect(status().isNoContent());
	}

	private String givenNewSportResult(Long studentID) {
		//language=JSON
		String sportResult = "{\n" +
			"  \"result\":6.6,\n" +
			"  \"discipline\":\"RUN_100\",\n" +
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