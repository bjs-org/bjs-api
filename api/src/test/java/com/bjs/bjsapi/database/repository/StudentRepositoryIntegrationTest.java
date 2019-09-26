package com.bjs.bjsapi.database.repository;

import static com.bjs.bjsapi.helper.ValidationFiles.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.UserPrivilege;
import com.bjs.bjsapi.database.model.helper.StudentBuilder;

public class StudentRepositoryIntegrationTest extends RepositoryIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ClassRepository classRepository;

	@Autowired
	private UserPrivilegeRepository userPrivilegeRepository;

	@Autowired
	private StudentRepository studentRepository;

	private User user;
	private Student privilegedStudent1;
	private Student unprivilegedStudent1;
	private Student privilegedStudent2;
	private Student unprivilegedStudent2;
	private Student privilegedStudent3;
	private Student unprivilegedStudent3;
	private Class privilegedClass;
	private Class unprivilegedClass;

	@Before
	public void setUp() throws Exception {
		setupTestUser();
		setupClassScenario();
	}

	@After
	public void tearDown() throws Exception {
		clearDB();
	}

	@Test
	public void test_findAll_unauthenticated() throws Exception {
		mvc.perform(get("/api/v1/students/"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "TestUser", password = "123456")
	public void test_findAll_authorized_privilegedData() throws Exception {

		String response = mvc.perform(get("/api/v1/students/").accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findAll-authorized-onlyPrivileged", mask(response, "/\\d+"));
	}

	@Test
	@WithMockUser(username = "TestUser", password = "123456", roles = { "ADMIN", "USER" })
	public void test_findAll_admin_allData() throws Exception {

		String response = mvc.perform(get("/api/v1/students/").accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findAll-admin-allData", mask(response, "/\\d+"));
	}

	@Test
	@WithMockUser(username = "test", password = "123456", roles = "USER")
	public void test_findById_unauthorized_unprivilegedData() throws Exception {
		mvc.perform(get("/api/v1/students/{id}", unprivilegedStudent1.getId()))
			.andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/students/{id}", unprivilegedStudent2.getId()))
			.andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/students/{id}", unprivilegedStudent3.getId()))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "TestUser", password = "123456")
	public void test_findById_authorized_privilegedData() throws Exception {
		mvc.perform(get("/api/v1/students/{id}", privilegedStudent1.getId()))
			.andExpect(status().isOk());
		mvc.perform(get("/api/v1/students/{id}", privilegedStudent2.getId()))
			.andExpect(status().isOk());
		mvc.perform(get("/api/v1/students/{id}", privilegedStudent3.getId()))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "TestUser", password = "123456", roles = { "USER", "ADMIN" })
	public void test_findById_adminAllData() throws Exception {
		mvc.perform(get("/api/v1/students/{id}", privilegedStudent1.getId()))
			.andExpect(status().isOk());
		mvc.perform(get("/api/v1/students/{id}", privilegedStudent2.getId()))
			.andExpect(status().isOk());
		mvc.perform(get("/api/v1/students/{id}", privilegedStudent3.getId()))
			.andExpect(status().isOk());
		mvc.perform(get("/api/v1/students/{id}", unprivilegedStudent1.getId()))
			.andExpect(status().isOk());
		mvc.perform(get("/api/v1/students/{id}", unprivilegedStudent2.getId()))
			.andExpect(status().isOk());
		mvc.perform(get("/api/v1/students/{id}", unprivilegedStudent3.getId()))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "TestUser", password = "123456")
	public void test_findByLastName_authorized_privilegedData() throws Exception {
		String response = mvc.perform(get("/api/v1/students/search/findByLastName?lastName=Student").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findByLastName-authorized-privilegedData", mask(response, "/\\d+"));
	}

	@Test
	@WithMockUser(username = "TestUser", password = "123456", roles = { "USER", "ADMIN" })
	public void test_findByLastName_admin_allData() throws Exception {
		String response = mvc.perform(get("/api/v1/students/search/findByLastName?lastName=Student").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findByLastName-admin-allData", mask(response, "/\\d+"));
	}

	@Test
	@WithMockUser(username = "TestUser", password = "123456", roles = { "USER", "ADMIN" })
	public void test_findByFirstName_admin_allData() throws Exception {
		String first = mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=first").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		String second = mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=second").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findByFirstName-first-admin-allData", mask(first, "/\\d+"));
		checkWithValidationFile("web/students-findByFirstName-second-admin-allData", mask(second, "/\\d+"));
	}

	@Test
	@WithMockUser(username = "TestUser", password = "123456")
	public void test_findByFirstName_authorized_privilegedData() throws Exception {
		String first = mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=first").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		String second = mvc.perform(get("/api/v1/students/search/findByFirstName?firstName=second").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findByFirstName-first-authorized-privilegedData", mask(first, "/\\d+"));
		checkWithValidationFile("web/students-findByFirstName-second-authorized-privilegedData", mask(second, "/\\d+"));
	}

	@Test
	@WithMockUser(username = "TestUser", password = "123456")
	public void test_findAllBySchoolClass_authorized_privilegedData() throws Exception {
		String response = mvc.perform(get("/api/v1/students/search/findAllBySchoolClass?schoolClass=/api/v1/classes/" + privilegedClass.getId()).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findBySchoolClass-authorized-privilegedData", mask(response, "/\\d+"));
	}

	@Test
	@WithMockUser(username = "TestUser", password = "123456", roles = { "USER", "ADMIN" })
	public void test_findAllBySchoolClass_admin_allData() throws Exception {
		String response = mvc.perform(get("/api/v1/students/search/findAllBySchoolClass?schoolClass=/api/v1/classes/" + unprivilegedClass.getId()).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findBySchoolClass-admin-allData", mask(response, "/\\d+"));
	}

	@Test
	@WithMockUser(username = "TestUser", password = "123456")
	public void test_findByFirstNameAndLastName_authorized_privilegedData() throws Exception {
		String first = mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=first&lastName=Student").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		String second = mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=second&lastName=Student").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findByFirstNameAndLastName-first-authorized-privilegedData", mask(first, "/\\d+"));
		checkWithValidationFile("web/students-findByFirstNameAndLastName-second-authorized-privilegedData", mask(second, "/\\d+"));
	}

	@Test
	@WithMockUser(username = "TestUser", password = "123456", roles = { "USER", "ADMIN" })
	public void test_findByFirstNameAndLastName_admin_allData() throws Exception {
		String first = mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=first&lastName=Student").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		String second = mvc.perform(get("/api/v1/students/search/findByFirstNameAndLastName?firstName=second&lastName=Student").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/students-findByFirstNameAndLastName-first-admin-allData", mask(first, "/\\d+"));
		checkWithValidationFile("web/students-findByFirstNameAndLastName-second-admin-allData", mask(second, "/\\d+"));
	}

	private void setupClassScenario() {
		privilegedClass = new Class("privilegedClass");
		classRepository.save(privilegedClass);
		unprivilegedClass = new Class("unprivilegedClass");
		classRepository.save(unprivilegedClass);

		privilegedStudent1 = new StudentBuilder()
			.setFirstName("first")
			.setLastName("Student")
			.setSchoolClass(privilegedClass)
			.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
			.setFemale(true)
			.createStudent();

		unprivilegedStudent1 = new StudentBuilder()
			.setFirstName("second")
			.setLastName("Student")
			.setSchoolClass(unprivilegedClass)
			.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
			.setFemale(true)
			.createStudent();

		privilegedStudent2 = new StudentBuilder()
			.setFirstName("third")
			.setLastName("Student")
			.setSchoolClass(privilegedClass)
			.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
			.setFemale(false)
			.createStudent();

		unprivilegedStudent2 = new StudentBuilder()
			.setFirstName("fourth")
			.setLastName("Student")
			.setSchoolClass(unprivilegedClass)
			.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
			.setFemale(false)
			.createStudent();

		privilegedStudent3 = new StudentBuilder()
			.setFirstName("fifth")
			.setLastName("Student")
			.setSchoolClass(privilegedClass)
			.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
			.setFemale(true)
			.createStudent();

		unprivilegedStudent3 = new StudentBuilder()
			.setFirstName("sixth")
			.setLastName("Student")
			.setSchoolClass(unprivilegedClass)
			.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
			.setFemale(true)
			.createStudent();

		studentRepository.saveAll(() -> Arrays.asList(privilegedStudent1, unprivilegedStudent1, privilegedStudent2, unprivilegedStudent2, privilegedStudent3, unprivilegedStudent3).iterator());

		userPrivilegeRepository.save(new UserPrivilege(user, privilegedClass));
	}

	private void setupTestUser() {
		user = new User("TestUser");
		user.setPassword(new BCryptPasswordEncoder().encode("123456"));

		userRepository.save(user);
	}

	private void clearDB() {
		userPrivilegeRepository.deleteAll();
		studentRepository.deleteAll();
		classRepository.deleteAll();
		userRepository.deleteAll();
	}

}