package com.bjs.bjsapi.database.repository;

import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;

import com.bjs.bjsapi.database.model.Student;

class StudentRepositoryIntegrationTest extends RepositoryIntegrationTest {

	private final ParameterDescriptor idDescriptor = parameterWithName("id").description("The student's id");
	private final ParameterDescriptor lastNameDescriptor = parameterWithName("lastName").description("The student's last name");
	private final ParameterDescriptor firstNameDescriptor = parameterWithName("firstName").description("The student's first name");
	private final ParameterDescriptor schoolClassDescriptor = parameterWithName("schoolClass").description("URI to the class");

	public static final List<FieldDescriptor> STUDENT_RESPONSE = Arrays.asList(
		fieldWithPath("firstName").type(JsonFieldType.STRING).description("The student's first name"),
		fieldWithPath("lastName").type(JsonFieldType.STRING).description("The students's last name"),
		fieldWithPath("birthDay").type(JsonFieldType.STRING).description("The student's birth day"),
		fieldWithPath("female").type(JsonFieldType.BOOLEAN).description("If the student is female"),
		subsectionWithPath("_links").description("Links regarding this student")
	);
	public static final List<FieldDescriptor> STUDENTS_RESPONSE = Arrays.asList(
		subsectionWithPath("_embedded.students").description("All (visible) students"),
		subsectionWithPath("_links").description("Links to other sections regarding students")
	);
	public static final List<FieldDescriptor> STUDENT_REQUEST = Arrays.asList(
		fieldWithPath("firstName").description("The student's first name"),
		fieldWithPath("lastName").description("The student's last name"),
		fieldWithPath("female").description("If the student is female"),
		fieldWithPath("schoolClass").description("The student's class as URI"),
		fieldWithPath("birthDay").description("The student's birth day in \"YYYY-mm-dd\"-format")
	);
	public static final List<FieldDescriptor> STUDENT_REQUEST_OPTIONAL = Arrays.asList(
		fieldWithPath("firstName").description("The student's first name").optional(),
		fieldWithPath("lastName").description("The student's last name").optional(),
		fieldWithPath("female").description("If the student is female").optional(),
		fieldWithPath("schoolClass").description("The student's class as URI").optional(),
		fieldWithPath("birthDay").description("The student's birth day in \"YYYY-mm-dd\"-format").optional()
	);

	@BeforeEach
	void setUp() throws Exception {
		super.setUp();
		JacksonTester.initFields(this, objectMapper);
		runAsAdmin(() -> {
			testData.setupStudents();
		});
	}

	@Test
	void test_findAll_unauthenticated() throws Exception {
		mvc.perform(get("/api/v1/students/")
			.with(anonymous()))
			.andExpect(status().isUnauthorized())
			.andDo(document("students-unauthenticated"));
	}

	@Test
	void test_findAll_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/students/")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", contains("Liam")))
			.andExpect(jsonPath("_embedded.students.[*].firstName", not(hasItems("Jalen"))))
			.andDo(document("students-get-all",
				responseFields(STUDENTS_RESPONSE).andWithPrefix("_embedded.students[].", STUDENT_RESPONSE)
			));
	}

	@Test
	void test_findAll_admin() throws Exception {
		mvc.perform(get("/api/v1/students/")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(2)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", containsInAnyOrder("Liam", "Jalen")));
	}

	@Test
	void test_findById_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/students/{id}", testData.inaccessibleStudent.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findById_authorized_privilegedData() throws Exception {
		mvc.perform(get("/api/v1/students/{id}", testData.accessibleStudent.getId())
			.with(asUser()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("firstName").value(testData.accessibleStudent.getFirstName()))
			.andExpect(jsonPath("lastName").value(testData.accessibleStudent.getLastName()))
			.andExpect(jsonPath("female").value(testData.accessibleStudent.getFemale()))
			.andDo(document("students-get-byId",
				pathParameters(idDescriptor),
				responseFields(STUDENT_RESPONSE),
				links(
					linkWithRel("self").description("Link to exactly this page"),
					linkWithRel("student").description("Link to the student"),
					linkWithRel("sportResults").description("Link to sport results belonging to the student"),
					linkWithRel("schoolClass").description("Link to school class the student belong to"),
					linkWithRel("score").description("Link to score of the student"),
					linkWithRel("classification").description("Link to classification of student")
				)
			));
	}

	@Test
	void test_findById_admin() throws Exception {
		for (Student student : Arrays.asList(testData.accessibleStudent, testData.inaccessibleStudent)) {
			mvc.perform(get("/api/v1/students/{id}", student.getId())
				.with(asAdmin()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("firstName").value(student.getFirstName()))
				.andExpect(jsonPath("lastName").value(student.getLastName()))
				.andExpect(jsonPath("female").value(student.getFemale()));
		}
	}

	@Test
	void test_findByLastName_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByLastName?lastName=Heß")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", contains("Liam")))
			.andExpect(jsonPath("_embedded.students.[*].firstName", not(hasItems("Jalen"))))
			.andDo(document("students-get-byLastName",
				requestParameters(lastNameDescriptor),
				responseFields(STUDENTS_RESPONSE).andWithPrefix("_embedded.students[].", STUDENT_RESPONSE)
			));
	}

	@Test
	void test_findByLastName_admin() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByLastName?lastName=Buscemi")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", containsInAnyOrder("Jalen")));
	}

	@Test
	void test_findByFirstName_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=Jalen")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(0)));
	}

	@Test
	void test_findByFirstName_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=Liam")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", hasItem("Liam")))
			.andExpect(jsonPath("_embedded.students.[*].lastName", hasItem("Heß")))
			.andDo(document("students-get-byFirstName",
				requestParameters(firstNameDescriptor),
				responseFields(STUDENTS_RESPONSE).andWithPrefix("_embedded.students[].", STUDENT_RESPONSE)
			));
	}

	@Test
	void test_findByFirstName_admin() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=Liam")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", hasItem("Liam")))
			.andExpect(jsonPath("_embedded.students.[*].lastName", hasItem("Heß")));

		mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=Jalen")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", hasItem("Jalen")))
			.andExpect(jsonPath("_embedded.students.[*].lastName", hasItem("Buscemi")));
	}

	@Test
	void test_findAllBySchoolClass_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/students/search/findAllBySchoolClass?schoolClass=/api/v1/classes/" + testData.accessibleClass.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", contains("Liam")))
			.andExpect(jsonPath("_embedded.students.[*].firstName", not(hasItems("Jalen"))))
			.andDo(document("students-get-bySchoolClass",
				requestParameters(schoolClassDescriptor),
				responseFields(STUDENTS_RESPONSE).andWithPrefix("_embedded.students[].", STUDENT_RESPONSE)
			));
	}

	@Test
	void test_findAllBySchoolClass_admin() throws Exception {
		mvc.perform(get("/api/v1/students/search/findAllBySchoolClass?schoolClass=/api/v1/classes/" + testData.inaccessibleClass.getId())
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", contains("Jalen")))
			.andExpect(jsonPath("_embedded.students.[*].firstName", not(hasItems("Liam"))));
	}

	@Test
	void test_findByFirstNameAndLastName_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=Jalen&lastName=Buscemi")
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(0)));
	}

	@Test
	void test_findByFirstNameAndLastName_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=Liam&lastName=Heß")
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", hasItem("Liam")))
			.andExpect(jsonPath("_embedded.students.[*].lastName", hasItem("Heß")))
			.andDo(document("students-get-byFirstNameAndLastName",
				requestParameters(firstNameDescriptor, lastNameDescriptor),
				responseFields(STUDENTS_RESPONSE).andWithPrefix("_embedded.students[].", STUDENT_RESPONSE)
			));
	}

	@Test
	void test_findByFirstNameAndLastName_admin() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=Liam&lastName=Heß")
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", hasItem("Liam")))
			.andExpect(jsonPath("_embedded.students.[*].lastName", hasItem("Heß")))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=Jalen&lastName=Buscemi")
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", hasItem("Jalen")))
			.andExpect(jsonPath("_embedded.students.[*].lastName", hasItem("Buscemi")));
	}

	@Test
	void test_create_unauthorized() throws Exception {
		String jsonStudent = IntegrationTestData.giveNewStudent(testData.inaccessibleClass.getId());

		mvc.perform(post("/api/v1/students")
			.content(jsonStudent)
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_create_userAuthorized() throws Exception {
		String jsonStudent = IntegrationTestData.giveNewStudent(testData.accessibleClass.getId());

		mvc.perform(post("/api/v1/students")
			.content(jsonStudent)
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andDo(document("students-post",
				requestFields(
					fieldWithPath("firstName").type(JsonFieldType.STRING).description("The student's first name"),
					fieldWithPath("lastName").type(JsonFieldType.STRING).description("The students's last name"),
					fieldWithPath("birthDay").type(JsonFieldType.STRING).description("The student's birth day"),
					fieldWithPath("female").type(JsonFieldType.BOOLEAN).description("Whether or not the student is female"),
					fieldWithPath("schoolClass").description("URL to school class the student belongs to")
				),
				responseFields(STUDENT_RESPONSE)))
			.andExpect(status().isCreated());
	}

	@Test
	void test_create_admin() throws Exception {
		String jsonStudent = IntegrationTestData.giveNewStudent(testData.inaccessibleClass.getId());

		mvc.perform(post("/api/v1/students")
			.content(jsonStudent)
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isCreated());
	}

	@Test
	void test_edit_unauthorized() throws Exception {
		String json = IntegrationTestData.giveNewStudent(testData.inaccessibleClass.getId());

		mvc.perform(patch("/api/v1/students/{id}", testData.inaccessibleStudent.getId())
			.content(json)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_edit_userAuthorized() throws Exception {
		String json = IntegrationTestData.giveNewStudent(testData.accessibleClass.getId());

		mvc.perform(patch("/api/v1/students/{id}", testData.accessibleStudent.getId())
			.content(json)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("firstName").value("Simon"))
			.andExpect(jsonPath("lastName").value("Schwarz"))
			.andExpect(jsonPath("female").value(false))
			.andDo(document("students-patch",
				pathParameters(idDescriptor),
				requestFields(STUDENT_REQUEST_OPTIONAL),
				responseFields(STUDENT_RESPONSE)
			));
	}

	@Test
	void test_edit_admin() throws Exception {
		String newFemale = "false";
		String newLastName = "new last name";
		String newFirstName = "new first name";

		String json = IntegrationTestData.giveNewStudent(newFirstName, newLastName, String.format("/api/v1/classes/%s", testData.accessibleClass.getId()), newFemale, "2002-01-10");

		mvc.perform(patch("/api/v1/students/{id}", testData.accessibleStudent.getId())
			.content(json)
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("firstName").value(newFirstName))
			.andExpect(jsonPath("lastName").value(newLastName))
			.andExpect(jsonPath("female").value(newFemale));

		mvc.perform(patch("/api/v1/students/{id}", testData.inaccessibleStudent.getId())
			.content(json)
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("firstName").value(newFirstName))
			.andExpect(jsonPath("lastName").value(newLastName))
			.andExpect(jsonPath("female").value(newFemale));
	}

	@Test
	void test_replace_authorized() throws Exception {
		String newFemale = "false";
		String newLastName = "new last name";
		String newFirstName = "new first name";

		String json = IntegrationTestData.giveNewStudent(newFirstName, newLastName, String.format("/api/v1/classes/%s", testData.accessibleClass.getId()), newFemale, "2002-01-10");

		mvc.perform(put("/api/v1/students/{id}", testData.accessibleStudent.getId())
			.content(json)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("firstName").value(newFirstName))
			.andExpect(jsonPath("lastName").value(newLastName))
			.andExpect(jsonPath("female").value(newFemale))
			.andDo(document("students-put",
				pathParameters(idDescriptor),
				requestFields(STUDENT_REQUEST),
				responseFields(STUDENT_RESPONSE)
			));
	}

	@Test
	void test_replace_unauthorized() throws Exception {
		String newFemale = "false";
		String newLastName = "new last name";
		String newFirstName = "new first name";

		String json = IntegrationTestData.giveNewStudent(newFirstName, newLastName, String.format("/api/v1/classes/%s", testData.accessibleClass.getId()), newFemale, "2002-01-10");

		mvc.perform(put("/api/v1/students/{id}", testData.inaccessibleStudent.getId())
			.content(json)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_replace_admin() throws Exception {
		String newFemale = "false";
		String newLastName = "new last name";
		String newFirstName = "new first name";

		String json = IntegrationTestData.giveNewStudent(newFirstName, newLastName, String.format("/api/v1/classes/%s", testData.accessibleClass.getId()), newFemale, "2002-01-10");

		mvc.perform(put("/api/v1/students/{id}", testData.inaccessibleStudent.getId())
			.content(json)
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("firstName").value(newFirstName))
			.andExpect(jsonPath("lastName").value(newLastName))
			.andExpect(jsonPath("female").value(newFemale));
	}

	@Test
	void test_delete_unauthorized() throws Exception {
		mvc.perform(delete("/api/v1/students/{id}", testData.inaccessibleStudent.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_delete_authorized() throws Exception {
		mvc.perform(delete("/api/v1/students/{id}", testData.accessibleStudent.getId())
			.with(asUser()))
			.andExpect(status().isNoContent())
			.andDo(document("students-delete", pathParameters(idDescriptor)));
	}

	@Test
	void test_delete_admin() throws Exception {
		mvc.perform(delete("/api/v1/students/{id}", testData.accessibleStudent.getId())
			.with(asAdmin()))
			.andExpect(status().isNoContent());

		mvc.perform(get("/api/v1/students/{id}", testData.accessibleStudent.getId())
			.with(asAdmin()))
			.andExpect(status().isNotFound());

		mvc.perform(delete("/api/v1/students/{id}", testData.inaccessibleStudent.getId())
			.with(asAdmin()))
			.andExpect(status().isNoContent());
	}

	@Test
	void test_get_class() throws Exception {
		mvc.perform(get("/api/v1/students/{id}/schoolClass", testData.accessibleStudent.getId())
			.with(asUser()))
			.andExpect(jsonPath("className").value("a"))
			.andDo(document("student-class-get",
				pathParameters(idDescriptor),
				responseFields(ClassRepositoryIntegrationTest.CLASS_RESPONSE)
			));
	}

	@Test
	void test_get_score() throws Exception {
		mvc.perform(get("/api/v1/students/{id}/score", testData.accessibleStudent.getId())
			.with(asUser()))
			.andExpect(content().string("0"))
			.andDo(document("student-score-get",
				pathParameters(idDescriptor)
			));
	}

}