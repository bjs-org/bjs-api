package com.bjs.bjsapi.database.repository;

import static com.bjs.bjsapi.helper.ValidationFiles.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.UserPrivilege;

public class ClassRepositoryIntegrationTest extends RepositoryIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ClassRepository classRepository;

	@Autowired
	private UserPrivilegeRepository userPrivilegeRepository;

	private User user;

	@Before
	public void setUp() throws Exception {
		user = new User();
		user.setUsername("test");
		user.setPassword(new BCryptPasswordEncoder().encode("123456"));

		userRepository.save(user);
	}

	@After
	public void tearDown() throws Exception {
		clearDB();
	}

	private void clearDB() {
		userPrivilegeRepository.deleteAll();
		classRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	public void test_findAll_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/classes"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "test", password = "123456", roles = "USER")
	public void test_findAll_authorized_onlyPrivilegedData() throws Exception {

		Class privilegedClass = new Class("privilegedClass");
		classRepository.save(privilegedClass);

		Class unprivilegedClass = new Class("unprivilegedClass");
		classRepository.save(unprivilegedClass);

		userPrivilegeRepository.save(new UserPrivilege(user, privilegedClass));

		MockHttpServletResponse response = mvc.perform(get("/api/v1/classes").accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse();

		checkWithValidationFile("web/classes-findAll-authorized-onlyPrivileged", mask(response.getContentAsString(), privilegedClass.getId().toString()));
	}

	@Test
	@WithMockUser(username = "test", password = "123456", roles = { "USER", "ADMIN" })
	public void test_findAll_admin_allData() throws Exception {

		Class unprivilegedClass1 = new Class("unprivilegedClass1");
		classRepository.save(unprivilegedClass1);

		Class unprivilegedClass2 = new Class("unprivilegedClass2");
		classRepository.save(unprivilegedClass2);

		MockHttpServletResponse response = mvc.perform(get("/api/v1/classes").accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse();

		checkWithValidationFile("web/classes-findAll-admin-allData", mask(response.getContentAsString(), unprivilegedClass1.getId().toString(), unprivilegedClass2.getId().toString()));
	}

	@Test
	@WithMockUser(username = "test", password = "123456", roles = "USER")
	public void test_findById_authorized_onlyPrivilegedData() throws Exception {

		Class privilegedClass = new Class("privilegedClass");
		classRepository.save(privilegedClass);

		Class unprivilegedClass = new Class("unprivilegedClass");
		classRepository.save(unprivilegedClass);

		userPrivilegeRepository.save(new UserPrivilege(user, privilegedClass));

		mvc.perform(get("/api/v1/classes/{id}", unprivilegedClass.getId()).accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isForbidden());

		mvc.perform(get("/api/v1/classes/{id}", privilegedClass.getId()).accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "test", password = "123456", roles = { "USER", "ADMIN" })
	public void test_findById_admin_allData() throws Exception {

		Class unprivilegedClass1 = new Class("unprivilegedClass1");
		classRepository.save(unprivilegedClass1);

		Class unprivilegedClass2 = new Class("unprivilegedClass2");
		classRepository.save(unprivilegedClass2);

		mvc.perform(get("/api/v1/classes/{id}", unprivilegedClass2.getId()).accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());

		mvc.perform(get("/api/v1/classes/{id}", unprivilegedClass1.getId()).accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "test", password = "123456", roles = "USER")
	public void test_findByName_authorized_onlyPrivilegedData() throws Exception {

		Class privilegedClass = new Class("privilegedClass");
		classRepository.save(privilegedClass);

		Class unprivilegedClass = new Class("unprivilegedClass");
		classRepository.save(unprivilegedClass);

		userPrivilegeRepository.save(new UserPrivilege(user, privilegedClass));

		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", unprivilegedClass.getClassName()).accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isForbidden());

		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", privilegedClass.getClassName()).accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "test", password = "123456", roles = { "USER", "ADMIN" })
	public void test_findByName_admin_allData() throws Exception {

		Class unprivilegedClass1 = new Class("unprivilegedClass1");
		classRepository.save(unprivilegedClass1);

		Class unprivilegedClass2 = new Class("unprivilegedClass2");
		classRepository.save(unprivilegedClass2);

		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", unprivilegedClass2.getClassName()).accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());

		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", unprivilegedClass1.getClassName()).accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "test", password = "123456", roles = { "USER" })
	public void test_findByClassTeacher_authorized_onlyPrivilegedData() throws Exception {

		Class privilegedClass = new Class("privilegedClass");
		privilegedClass.setClassTeacherName("ClassTeacherus");
		classRepository.save(privilegedClass);

		Class unprivilegedClass = new Class("unprivilegedClass");
		unprivilegedClass.setClassTeacherName("ClassTeacherus");
		classRepository.save(unprivilegedClass);

		userPrivilegeRepository.save(new UserPrivilege(user, privilegedClass));

		MockHttpServletResponse response1 = mvc.perform(get("/api/v1/classes/search/findByClassTeacherName?classTeacherName={classTeacherName}", "ClassTeacherus").accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse();

		checkWithValidationFile("web/classes-findByClassTeacher-authorized-onlyPrivileged", mask(response1.getContentAsString(), privilegedClass.getId().toString(), unprivilegedClass.getId().toString()));
	}

	@Test
	@WithMockUser(username = "test", password = "123456", roles = { "USER", "ADMIN" })
	public void test_findByClassTeacher_admin_allData() throws Exception {

		Class unprivilegedClass1 = new Class("unprivilegedClass1");
		unprivilegedClass1.setClassTeacherName("ClassTeacherus");
		classRepository.save(unprivilegedClass1);

		Class unprivilegedClass2 = new Class("unprivilegedClass2");
		unprivilegedClass2.setClassTeacherName("ClassTeacherus");
		classRepository.save(unprivilegedClass2);

		String response = mvc.perform(get("/api/v1/classes/search/findByClassTeacherName?classTeacherName={classTeacherName}", "ClassTeacherus").accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/classes-findByClassTeacher-admin-allData", mask(response, unprivilegedClass1.getId().toString(), unprivilegedClass2.getId().toString()));
	}

}
