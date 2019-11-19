package com.bjs.bjsapi.database.repository;

import static com.bjs.bjsapi.helper.ValidationFiles.*;
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
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StudentRepositoryIntegrationTest extends RepositoryIntegrationTest {

	private Student privilegedStudent1;
	private Student unprivilegedStudent1;
	private Student privilegedStudent2;
	private Student unprivilegedStudent2;
	private Student privilegedStudent3;
	private Student unprivilegedStudent3;
	private Class privilegedClass;
	private Class unprivilegedClass;

	private final ParameterDescriptor idDescriptor = parameterWithName("id").description("The student's id");
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

	private JacksonTester<Student> jacksonTester;
	private ParameterDescriptor lastNameDescriptor = parameterWithName("lastName").description("The student's last name");
	private ParameterDescriptor firstNameDescriptor = parameterWithName("firstName").description("The student's first name");

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
	void test_findByFirstName_admin() throws Exception {
		String first = mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=first")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse().getContentAsString();

		String second = mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=second")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findByFirstName-first-admin-allData", mask(first));
		checkWithValidationFile("web/students-findByFirstName-second-admin-allData", mask(second));
	}

	@Test
	void test_findByFirstName_userAuthorized() throws Exception {
		String first = mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=first")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("students-get-byFirstName",
				requestParameters(
					firstNameDescriptor
				),
				responseFields(
					subsectionWithPath("_embedded.students").description("All (visible) students with given first name"),
					subsectionWithPath("_links").description("All links regarding this search")
				).andWithPrefix("_embedded.students[].", studentResponse)))
			.andReturn().getResponse().getContentAsString();

		String second = mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=second")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findByFirstName-first-authorized-privilegedData", mask(first));
		checkWithValidationFile("web/students-findByFirstName-second-authorized-privilegedData", mask(second));
	}

	@Test
	void test_findAllBySchoolClass_userAuthorized() throws Exception {
		String response = mvc.perform(get("/api/v1/students/search/findAllBySchoolClass?schoolClass=/api/v1/classes/" + privilegedClass.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("students-get-bySchoolClass",
				requestParameters(
					parameterWithName("schoolClass").description("URL to the class object")
				),
				responseFields(
					subsectionWithPath("_embedded.students").description("All (visible) students belonging to the given school class"),
					subsectionWithPath("_links").description("All links regarding this search")
				).andWithPrefix("_embedded.students[].", studentResponse)))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findBySchoolClass-authorized-privilegedData", mask(response));
	}

	@Test
	void test_findAllBySchoolClass_admin() throws Exception {
		String response = mvc.perform(get("/api/v1/students/search/findAllBySchoolClass?schoolClass=/api/v1/classes/" + unprivilegedClass.getId())
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findBySchoolClass-admin-allData", mask(response));
	}

	@Test
	void test_findByFirstNameAndLastName_userAuthorized() throws Exception {
		String first = mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=first&lastName=Student")
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("students-get-byFirstNameAndLastName",
				requestParameters(firstNameDescriptor, lastNameDescriptor),
				responseFields(studentsResponse).andWithPrefix("_embedded.students[].", studentResponse)))
			.andReturn().getResponse().getContentAsString();

		String second = mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=second&lastName=Student")
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findByFirstNameAndLastName-first-authorized-privilegedData", mask(first));
		checkWithValidationFile("web/students-findByFirstNameAndLastName-second-authorized-privilegedData", mask(second));
	}

	@Test
	void test_findByFirstNameAndLastName_admin() throws Exception {
		String first = mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=first&lastName=Student")
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse().getContentAsString();

		String second = mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=second&lastName=Student")
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findByFirstNameAndLastName-first-admin-allData", mask(first));
		checkWithValidationFile("web/students-findByFirstNameAndLastName-second-admin-allData", mask(second));
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
	void test_create_unauthorized() throws Exception {
		//TODO problem with method security

		String jsonStudent = givenNewStudent(unprivilegedClass.getId());

		mvc.perform(post("/api/v1/students")
			.content(jsonStudent)
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_edit_userAuthorized() throws Exception {
		String newSchoolClass = String.format("/api/v1/classes/%s", privilegedClass.getId());
		String newFemale = "false";
		String newLastName = "new last name";
		String newFirstName = "new first name";
		String birthDay = "2002-01-10";

		String json = givenJsonStudent(newSchoolClass, newFemale, newLastName, newFirstName, birthDay);

		mvc.perform(patch("/api/v1/students/{id}", privilegedStudent1.getId())
			.content(json)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("firstName").value(newFirstName))
			.andExpect(jsonPath("lastName").value(newLastName))
			.andExpect(jsonPath("female").value(newFemale))
			.andDo(document("students-patch",
				pathParameters(
					parameterWithName("id").description("The student's id")
				), requestFields(
					fieldWithPath("firstName").description("The student's first name").optional(),
					fieldWithPath("lastName").description("The student's last name").optional(),
					fieldWithPath("female").description("Whether or not the student is female").optional(),
					fieldWithPath("schoolClass").description("The student's class as URL").optional(),
					fieldWithPath("birthDay").description("The student's birth day in \"YYYY-mm-dd\"-format").optional()
				), responseFields(studentResponse)));

		mvc.perform(patch("/api/v1/students/{id}", unprivilegedStudent1.getId())
			.content(json)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_edit_admin() throws Exception {
		String newSchoolClass = String.format("/api/v1/classes/%s", privilegedClass.getId());
		String newFemale = "false";
		String newLastName = "new last name";
		String newFirstName = "new first name";
		String birthDay = "2002-01-10";

		String json = givenJsonStudent(newSchoolClass, newFemale, newLastName, newFirstName, birthDay);

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
		String newSchoolClass = String.format("/api/v1/classes/%s", privilegedClass.getId());
		String newFemale = "false";
		String newLastName = "new last name";
		String newFirstName = "new first name";
		String birthDay = "2002-01-10";

		String json = givenJsonStudent(newSchoolClass, newFemale, newLastName, newFirstName, birthDay);

		mvc.perform(put("/api/v1/students/{id}", privilegedStudent1.getId())
			.content(json)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("firstName").value(newFirstName))
			.andExpect(jsonPath("lastName").value(newLastName))
			.andExpect(jsonPath("female").value(newFemale))
			.andDo(document("students-put",
				pathParameters(
					parameterWithName("id").description("The student's id")
				), requestFields(
					fieldWithPath("firstName").description("The student's first name"),
					fieldWithPath("lastName").description("The student's last name"),
					fieldWithPath("female").description("Whether or not the student is female"),
					fieldWithPath("schoolClass").description("The student's class as URL"),
					fieldWithPath("birthDay").description("The student's birth day in \"YYYY-mm-dd\"-format")
				), responseFields(studentResponse)));
	}

	@Test
	void test_replace_unauthorized() throws Exception {
		String newSchoolClass = String.format("/api/v1/classes/%s", privilegedClass.getId());
		String newFemale = "false";
		String newLastName = "new last name";
		String newFirstName = "new first name";
		String birthDay = "2002-01-10";

		String json = givenJsonStudent(newSchoolClass, newFemale, newLastName, newFirstName, birthDay);

		mvc.perform(put("/api/v1/students/{id}", unprivilegedStudent1.getId())
			.content(json)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_replace_admin() throws Exception {
		String newSchoolClass = String.format("/api/v1/classes/%s", privilegedClass.getId());
		String newFemale = "false";
		String newLastName = "new last name";
		String newFirstName = "new first name";
		String birthDay = "2002-01-10";

		String json = givenJsonStudent(newSchoolClass, newFemale, newLastName, newFirstName, birthDay);

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
		Student student = new StudentBuilder()
			.setFirstName("Simon")
			.setLastName("Schwarz")
			.setFemale(false)
			.setBirthDay(Date.valueOf(LocalDate.of(2002, 1, 25)))
			.createStudent();

		ObjectNode schoolClass = ((ObjectNode) objectMapper.readTree(jacksonTester.write(student).getJson()));
		schoolClass.put("schoolClass", String.format("/api/v1/classes/%d", schoolClassID));
		schoolClass.remove("id");
		return schoolClass.toString();
	}

}