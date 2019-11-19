package com.bjs.bjsapi.database.repository;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.helper.ClassBuilder;
import com.bjs.bjsapi.database.model.helper.StudentBuilder;
import com.bjs.bjsapi.database.model.helper.UserPrivilegeBuilder;
import com.bjs.bjsapi.helper.SecurityHelper;

class StudentRepositoryIntegrationTest extends RepositoryIntegrationTest {

	private Student privilegedStudent1;
	private Student unprivilegedStudent1;
	private Student privilegedStudent2;
	private Student unprivilegedStudent2;
	private Student privilegedStudent3;
	private Student unprivilegedStudent3;

	private Class privilegedClass;
	private Class unprivilegedClass;

	private final ParameterDescriptor idDescriptor = parameterWithName("id").description("The student's id");
	private ParameterDescriptor lastNameDescriptor = parameterWithName("lastName").description("The student's last name");
	private ParameterDescriptor firstNameDescriptor = parameterWithName("firstName").description("The student's first name");
	private ParameterDescriptor schoolClassDescriptor = parameterWithName("schoolClass").description("URI to the class");

	private final List<FieldDescriptor> studentResponse = Arrays.asList(
		fieldWithPath("firstName").type(JsonFieldType.STRING).description("The student's first name"),
		fieldWithPath("lastName").type(JsonFieldType.STRING).description("The students's last name"),
		fieldWithPath("birthDay").type(JsonFieldType.STRING).description("The student's birth day"),
		fieldWithPath("female").type(JsonFieldType.BOOLEAN).description("If the student is female"),
		subsectionWithPath("_links").description("Links regarding this student")
	);
	private final List<FieldDescriptor> studentsResponse = Arrays.asList(
		subsectionWithPath("_embedded.students").description("All (visible) students"),
		subsectionWithPath("_links").description("Links to other sections regarding students")
	);
	private final List<FieldDescriptor> studentRequest = Arrays.asList(
		fieldWithPath("firstName").description("The student's first name"),
		fieldWithPath("lastName").description("The student's last name"),
		fieldWithPath("female").description("If the student is female"),
		fieldWithPath("schoolClass").description("The student's class as URI"),
		fieldWithPath("birthDay").description("The student's birth day in \"YYYY-mm-dd\"-format")
	);
	private final List<FieldDescriptor> studentRequestOptional = Arrays.asList(
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
		setupClassScenario();
		SecurityHelper.reset();
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
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(3)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", contains("first", "third", "fifth")))
			.andExpect(jsonPath("_embedded.students.[*].firstName", not(hasItems("second", "fourth", "sixth"))))
			.andDo(document("students-get-all",
				responseFields(studentsResponse).andWithPrefix("_embedded.students[].", studentResponse)
			));
	}

	@Test
	void test_findAll_admin() throws Exception {
		mvc.perform(get("/api/v1/students/")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(6)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", containsInAnyOrder("first", "third", "fifth", "second", "fourth", "sixth")));
	}

	@Test
	void test_findById_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/students/{id}", unprivilegedStudent1.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/students/{id}", unprivilegedStudent2.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/students/{id}", unprivilegedStudent3.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findById_authorized_privilegedData() throws Exception {
		mvc.perform(get("/api/v1/students/{id}", privilegedStudent1.getId())
			.with(asUser()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("firstName").value(privilegedStudent1.getFirstName()))
			.andExpect(jsonPath("lastName").value(privilegedStudent1.getLastName()))
			.andExpect(jsonPath("female").value(privilegedStudent1.getFemale()))
			.andDo(document("students-get-byId",
				pathParameters(idDescriptor),
				responseFields(studentResponse)
			));

		mvc.perform(get("/api/v1/students/{id}", privilegedStudent2.getId())
			.with(asUser()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("firstName").value(privilegedStudent2.getFirstName()))
			.andExpect(jsonPath("lastName").value(privilegedStudent2.getLastName()))
			.andExpect(jsonPath("female").value(privilegedStudent2.getFemale()));

		mvc.perform(get("/api/v1/students/{id}", privilegedStudent3.getId())
			.with(asUser()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("firstName").value(privilegedStudent3.getFirstName()))
			.andExpect(jsonPath("lastName").value(privilegedStudent3.getLastName()))
			.andExpect(jsonPath("female").value(privilegedStudent3.getFemale()));
	}

	@Test
	void test_findById_admin() throws Exception {
		for (Student student : Arrays.asList(privilegedStudent1, privilegedStudent2, privilegedStudent3, unprivilegedStudent1, unprivilegedStudent2, unprivilegedStudent3)) {
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
		mvc.perform(get("/api/v1/students/search/findByLastName?lastName=Student")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(3)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", contains("first", "third", "fifth")))
			.andExpect(jsonPath("_embedded.students.[*].firstName", not(hasItems("second", "fourth", "sixth"))))
			.andDo(document("students-get-byLastName",
				requestParameters(lastNameDescriptor),
				responseFields(studentsResponse).andWithPrefix("_embedded.students[].", studentResponse)
			));
	}

	@Test
	void test_findByLastName_admin() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByLastName?lastName=Student")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(6)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", containsInAnyOrder("first", "third", "fifth", "second", "fourth", "sixth")));
	}

	@Test
	void test_findByFirstName_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=second")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(0)));
	}

	@Test
	void test_findByFirstName_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=first")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", hasItem("first")))
			.andExpect(jsonPath("_embedded.students.[*].lastName", hasItem("Student")))
			.andDo(document("students-get-byFirstName",
				requestParameters(firstNameDescriptor),
				responseFields(studentsResponse).andWithPrefix("_embedded.students[].", studentResponse)
			));
	}

	@Test
	void test_findByFirstName_admin() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=first")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", hasItem("first")))
			.andExpect(jsonPath("_embedded.students.[*].lastName", hasItem("Student")));

		mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=second")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", hasItem("second")))
			.andExpect(jsonPath("_embedded.students.[*].lastName", hasItem("Student")));
	}

	@Test
	void test_findAllBySchoolClass_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/students/search/findAllBySchoolClass?schoolClass=/api/v1/classes/" + privilegedClass.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(3)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", contains("first", "third", "fifth")))
			.andExpect(jsonPath("_embedded.students.[*].firstName", not(hasItems("second", "fourth", "sixth"))))
			.andDo(document("students-get-bySchoolClass",
				requestParameters(schoolClassDescriptor),
				responseFields(studentsResponse).andWithPrefix("_embedded.students[].", studentResponse)
			));
	}

	@Test
	void test_findAllBySchoolClass_admin() throws Exception {
		mvc.perform(get("/api/v1/students/search/findAllBySchoolClass?schoolClass=/api/v1/classes/" + unprivilegedClass.getId())
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(3)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", contains("second", "fourth", "sixth")))
			.andExpect(jsonPath("_embedded.students.[*].firstName", not(hasItems("first", "third", "fifth"))));
	}

	@Test
	void test_findByFirstNameAndLastName_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=second&lastName=Student")
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(0)));
	}

	@Test
	void test_findByFirstNameAndLastName_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=first&lastName=Student")
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", hasItem("first")))
			.andExpect(jsonPath("_embedded.students.[*].lastName", hasItem("Student")))
			.andDo(document("students-get-byFirstNameAndLastName",
				requestParameters(firstNameDescriptor, lastNameDescriptor),
				responseFields(studentsResponse).andWithPrefix("_embedded.students[].", studentResponse)
			));
	}

	@Test
	void test_findByFirstNameAndLastName_admin() throws Exception {
		mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=first&lastName=Student")
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", hasItem("first")))
			.andExpect(jsonPath("_embedded.students.[*].lastName", hasItem("Student")))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=second&lastName=Student")
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("_embedded.students.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.students.[*].firstName", hasItem("second")))
			.andExpect(jsonPath("_embedded.students.[*].lastName", hasItem("Student")));
	}

	@Test
	void test_create_unauthorized() throws Exception {
		String jsonStudent = givenNewStudent(unprivilegedClass.getId());

		mvc.perform(post("/api/v1/students")
			.content(jsonStudent)
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_create_userAuthorized() throws Exception {
		String jsonStudent = givenNewStudent(privilegedClass.getId());

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
				responseFields(studentResponse)))
			.andExpect(status().isCreated());
	}

	@Test
	void test_create_admin() throws Exception {
		String jsonStudent = givenNewStudent(unprivilegedClass.getId());

		mvc.perform(post("/api/v1/students")
			.content(jsonStudent)
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isCreated());
	}

	@Test
	void test_edit_unauthorized() throws Exception {
		String newSchoolClass = String.format("/api/v1/classes/%s", privilegedClass.getId());
		String newFemale = "false";
		String newLastName = "new last name";
		String newFirstName = "new first name";
		String birthDay = "2002-01-10";

		String json = givenJsonStudent(newSchoolClass, newFemale, newLastName, newFirstName, birthDay);

		mvc.perform(patch("/api/v1/students/{id}", unprivilegedStudent1.getId())
			.content(json)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_edit_userAuthorized() throws Exception {
		String newFemale = "false";
		String newLastName = "new last name";
		String newFirstName = "new first name";

		String json = givenJsonStudent(String.format("/api/v1/classes/%s", privilegedClass.getId()), newFemale, newLastName, newFirstName, "2002-01-10");

		mvc.perform(patch("/api/v1/students/{id}", privilegedStudent1.getId())
			.content(json)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("firstName").value(newFirstName))
			.andExpect(jsonPath("lastName").value(newLastName))
			.andExpect(jsonPath("female").value(newFemale))
			.andDo(document("students-patch",
				pathParameters(idDescriptor),
				requestFields(studentRequestOptional),
				responseFields(studentResponse)
			));
	}

	@Test
	void test_edit_admin() throws Exception {
		String newFemale = "false";
		String newLastName = "new last name";
		String newFirstName = "new first name";

		String json = givenJsonStudent(String.format("/api/v1/classes/%s", privilegedClass.getId()), newFemale, newLastName, newFirstName, "2002-01-10");

		mvc.perform(patch("/api/v1/students/{id}", privilegedStudent1.getId())
			.content(json)
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("firstName").value(newFirstName))
			.andExpect(jsonPath("lastName").value(newLastName))
			.andExpect(jsonPath("female").value(newFemale));

		mvc.perform(patch("/api/v1/students/{id}", unprivilegedStudent1.getId())
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

		String json = givenJsonStudent(String.format("/api/v1/classes/%s", privilegedClass.getId()), newFemale, newLastName, newFirstName, "2002-01-10");

		mvc.perform(put("/api/v1/students/{id}", privilegedStudent1.getId())
			.content(json)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("firstName").value(newFirstName))
			.andExpect(jsonPath("lastName").value(newLastName))
			.andExpect(jsonPath("female").value(newFemale))
			.andDo(document("students-put",
				pathParameters(idDescriptor),
				requestFields(studentRequest),
				responseFields(studentResponse)
			));
	}

	@Test
	void test_replace_unauthorized() throws Exception {
		String newFemale = "false";
		String newLastName = "new last name";
		String newFirstName = "new first name";

		String json = givenJsonStudent(String.format("/api/v1/classes/%s", privilegedClass.getId()), newFemale, newLastName, newFirstName, "2002-01-10");

		mvc.perform(put("/api/v1/students/{id}", unprivilegedStudent1.getId())
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

		String json = givenJsonStudent(String.format("/api/v1/classes/%s", privilegedClass.getId()), newFemale, newLastName, newFirstName, "2002-01-10");

		mvc.perform(put("/api/v1/students/{id}", unprivilegedStudent1.getId())
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
		mvc.perform(delete("/api/v1/students/{id}", unprivilegedStudent1.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_delete_authorized() throws Exception {
		mvc.perform(delete("/api/v1/students/{id}", privilegedStudent1.getId())
			.with(asUser()))
			.andExpect(status().isNoContent())
			.andDo(document("students-delete", pathParameters(idDescriptor)));
	}

	@Test
	void test_delete_admin() throws Exception {
		mvc.perform(delete("/api/v1/students/{id}", privilegedStudent1.getId())
			.with(asAdmin()))
			.andExpect(status().isNoContent());

		mvc.perform(delete("/api/v1/students/{id}", unprivilegedStudent1.getId())
			.with(asAdmin()))
			.andExpect(status().isNoContent());
	}

	private void setupClassScenario() {
		SecurityHelper.runAs("admin", "admin", "ROLE_USER", "ROLE_ADMIN");

		privilegedClass = classRepository.save(new ClassBuilder().setClassName("privilegedClass").createClass());
		unprivilegedClass = classRepository.save(new ClassBuilder().setClassName("unprivilegedClass").createClass());

		privilegedStudent1 = studentRepository.save(new StudentBuilder()
			.setFirstName("first")
			.setLastName("Student")
			.setSchoolClass(privilegedClass)
			.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
			.setFemale(true)
			.createStudent());

		unprivilegedStudent1 = studentRepository.save(new StudentBuilder()
			.setFirstName("second")
			.setLastName("Student")
			.setSchoolClass(unprivilegedClass)
			.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
			.setFemale(true)
			.createStudent());

		privilegedStudent2 = studentRepository.save(new StudentBuilder()
			.setFirstName("third")
			.setLastName("Student")
			.setSchoolClass(privilegedClass)
			.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
			.setFemale(false)
			.createStudent());

		unprivilegedStudent2 = studentRepository.save(new StudentBuilder()
			.setFirstName("fourth")
			.setLastName("Student")
			.setSchoolClass(unprivilegedClass)
			.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
			.setFemale(false)
			.createStudent());

		privilegedStudent3 = studentRepository.save(new StudentBuilder()
			.setFirstName("fifth")
			.setLastName("Student")
			.setSchoolClass(privilegedClass)
			.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
			.setFemale(true)
			.createStudent());

		unprivilegedStudent3 = studentRepository.save(new StudentBuilder()
			.setFirstName("sixth")
			.setLastName("Student")
			.setSchoolClass(unprivilegedClass)
			.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
			.setFemale(true)
			.createStudent());

		userPrivilegeRepository.save(new UserPrivilegeBuilder().setUser(user).setAccessibleClass(privilegedClass).createUserPrivilege());
	}

	private String givenJsonStudent(String newSchoolClass, String newFemale, String newLastName, String newFirstName, String birthDay) {
		return String.format("{\n  \"firstName\": \"%s\",\n  \"lastName\": \"%s\",\n  \"female\": %s,\n  \"birthDay\": \"%s\",\n  \"schoolClass\": \"%s\"\n}", newFirstName, newLastName, newFemale, birthDay, newSchoolClass);
	}

	private String givenNewStudent(Long schoolClassID) throws IOException {
		return givenJsonStudent(String.format("/api/v1/classes/%s", schoolClassID), "false", "Schwarz", "Simon", "2002-01-27");
	}

}