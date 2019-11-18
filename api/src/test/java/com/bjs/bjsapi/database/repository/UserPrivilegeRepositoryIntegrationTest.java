package com.bjs.bjsapi.database.repository;

import static org.hamcrest.Matchers.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultHandler;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.UserPrivilege;
import com.bjs.bjsapi.database.model.helper.ClassBuilder;
import com.bjs.bjsapi.database.model.helper.UserBuilder;
import com.bjs.bjsapi.database.model.helper.UserPrivilegeBuilder;
import com.bjs.bjsapi.helper.SecurityHelper;

public class UserPrivilegeRepositoryIntegrationTest extends RepositoryIntegrationTest {

	private final Logger log = LoggerFactory.getLogger(UserPrivilegeRepositoryIntegrationTest.class);
	private final ResultHandler PRINT_HANDLER = result -> log.info(result.getResponse().getContentAsString());
	private UserPrivilege userPrivilege;
	private Class schoolClass;
	private User testUser;
	private List<FieldDescriptor> userPrivilegeDescriptors = Arrays.asList(
		subsectionWithPath("_links").description("All links regarding this privilege object"),
		fieldWithPath("_links.accessibleClass").description("Link to class this privilege refers"),
		fieldWithPath("_links.user").description("Link to the user which this privilege has an effect")
	);

	@Override
	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
		setupUserPrivilegeScenario();
		SecurityHelper.reset();
	}

	@Test
	void test_findAll_unauthenticated() throws Exception {
		mvc.perform(get("/api/v1/user_privileges")
			.accept(MediaType.APPLICATION_JSON)
			.with(anonymous()))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_findAll_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/user_privileges")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findAll_admin() throws Exception {
		mvc.perform(get("/api/v1/user_privileges")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._embedded.user_privileges").isArray())
			.andExpect(jsonPath("$._embedded.user_privileges.[*]", hasSize(1)))
			.andExpect(jsonPath("$._embedded.user_privileges.[0]._links.self.href").exists())
			.andExpect(jsonPath("$._embedded.user_privileges.[0]._links.self.href").value(containsString("/api/v1/user_privileges/")))
			.andExpect(jsonPath("$._embedded.user_privileges.[0]._links.user.href").exists())
			.andExpect(jsonPath("$._embedded.user_privileges.[0]._links.accessibleClass.href").exists())
			.andExpect(jsonPath("$._links.self.href").value(endsWith("/api/v1/user_privileges")))
			.andExpect(jsonPath("$._links.search.href").value(endsWith("/api/v1/user_privileges/search")))
			.andDo(document("user-privileges-get-all",
				responseFields(
					subsectionWithPath("_links").description("Links to all other resources of user-privileges"),
					subsectionWithPath("_embedded.user_privileges").description("Array of all user-privileges")
				).andWithPrefix("_embedded.user_privileges[].", userPrivilegeDescriptors)
			));
	}

	@Test
	void test_findById_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/user_privileges/{id}", userPrivilege.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findById_admin() throws Exception {
		mvc.perform(get("/api/v1/user_privileges/{id}", userPrivilege.getId())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_links.self.href").value(containsString("/api/v1/user_privileges/")))
			.andExpect(jsonPath("_links.accessibleClass.href").exists())
			.andExpect(jsonPath("_links.user.href").exists())
			.andDo(document("user-privileges-get-byId",
				pathParameters(
					parameterWithName("id").description("The ID of the user privilege")
				),
				responseFields(userPrivilegeDescriptors)
			));
	}

	@Test
	void test_findByAccessibleClass_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/user_privileges/search/findByAccessibleClass?accessibleClass={accessibleClass}", "/" + schoolClass.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	private void setupUserPrivilegeScenario() {
		SecurityHelper.runAs("admin", "admin", "ROLE_ADMIN", "ROLE_USER");

		testUser = userRepository.save(new UserBuilder()
			.setUsername("helper")
			.setPassword("I'm Helping")
			.createUser());

		schoolClass = classRepository.save(new ClassBuilder()
			.setClassName("7D")
			.setClassTeacherName("A Class Teacher")
			.createClass());

		userPrivilege = userPrivilegeRepository.save(new UserPrivilegeBuilder()
			.setUser(testUser)
			.setAccessibleClass(schoolClass)
			.createUserPrivilege()
		);

		SecurityHelper.reset();
	}

}