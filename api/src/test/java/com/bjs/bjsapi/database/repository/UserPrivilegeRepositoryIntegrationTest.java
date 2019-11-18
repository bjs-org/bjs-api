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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;
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

	private final List<FieldDescriptor> userPrivilegeResponse = Arrays.asList(
		subsectionWithPath("_links").description("All links regarding this privilege object"),
		fieldWithPath("_links.accessibleClass").description("Link to class this privilege refers"),
		fieldWithPath("_links.user").description("Link to the user which this privilege has an effect")
	);

	private final List<FieldDescriptor> userPrivilegeRequest = Arrays.asList(
		fieldWithPath("accessibleClass").description("URI to class which user should get access to"),
		fieldWithPath("user").description("URI to user which should be able to access the class")
	);

	private final List<FieldDescriptor> userPrivilegeRequestOptional = Arrays.asList(
		fieldWithPath("accessibleClass").description("URI to class which user should get access to").optional().type(JsonFieldType.STRING),
		fieldWithPath("user").description("URI to user which should be able to access the class").optional().type(JsonFieldType.STRING)
	);
	private ParameterDescriptor idDescriptor = parameterWithName("id").description("The ID of the user privilege");

	@Override
	@BeforeEach
	void setUp() throws Exception {
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
				).andWithPrefix("_embedded.user_privileges[].", userPrivilegeResponse)
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
				pathParameters(idDescriptor),
				responseFields(userPrivilegeResponse)
			));
	}

	@Test
	void test_findByAccessibleClass_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/user_privileges/search/findByAccessibleClass?accessibleClass={accessibleClass}", "/" + schoolClass.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findByAccessibleClass_admin() throws Exception {
		mvc.perform(get("/api/v1/user_privileges/search/findByAccessibleClass?accessibleClass=api/v1/classes/" + schoolClass.getId())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._embedded.user_privileges.[*]", hasSize(1)))
			.andDo(document("user-privilege-get-by-class",
				requestParameters(
					parameterWithName("accessibleClass").description("URI to the class")
				), responseFields(
					subsectionWithPath("_links").description("Links to all other resources of user-privileges"),
					subsectionWithPath("_embedded.user_privileges").description("Array of all user-privileges concerning the given class")
				).andWithPrefix("_embedded.user_privileges[].", userPrivilegeResponse)
			));
	}

	@Test
	void test_findByUser_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/user_privileges/search/findByUser?user=api/v1/users/" + testUser.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findByUser_admin() throws Exception {
		mvc.perform(get("/api/v1/user_privileges/search/findByUser?user=api/v1/users/" + testUser.getId())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._embedded.user_privileges.[*]", hasSize(1)))
			.andDo(document("user-privilege-get-by-user",
				requestParameters(
					parameterWithName("user").description("URI to the user")
				), responseFields(
					subsectionWithPath("_links").description("Links to all other resources of user-privileges"),
					subsectionWithPath("_embedded.user_privileges").description("Array of all user-privileges concerning the given user")
				).andWithPrefix("_embedded.user_privileges[].", userPrivilegeResponse)
			));
	}

	@Test
	void test_create_unauthorized() throws Exception {
		mvc.perform(post("/api/v1/user_privileges/")
			.content(givenNewUserPrivilege())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_create_authorized() throws Exception {
		mvc.perform(post("/api/v1/user_privileges/")
			.content(givenNewUserPrivilege())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$._links.accessibleClass.href").isString())
			.andExpect(jsonPath("$._links.user.href").isString())
			.andDo(document("user-privileges-post",
				requestFields(userPrivilegeRequest),
				responseFields(userPrivilegeResponse)
			));
	}

	@Test
	void test_edit_unauthorized() throws Exception {
		mvc.perform(patch("/api/v1/user_privileges/{id}", userPrivilege.getId())
			.content(givenChangedClassUserPrivilege())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_edit_admin() throws Exception {
		mvc.perform(patch("/api/v1/user_privileges/{id}", userPrivilege.getId())
			.content(givenChangedClassUserPrivilege())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("user-privileges-patch",
				pathParameters(idDescriptor),
				requestFields(userPrivilegeRequestOptional),
				responseFields(userPrivilegeResponse)
			));
	}

	private String givenChangedClassUserPrivilege() {
		//language=JSON
		String userPrivilegeJsonTemplate = "{\n" +
			"  \"accessibleClass\": \"/api/v1/classes/%s\"\n" +
			"}";

		return String.format(userPrivilegeJsonTemplate, schoolClass.getId());
	}

	private String givenNewUserPrivilege() {
		//language=JSON
		String userPrivilegeJsonTemplate = "{\n" +
			"  \"accessibleClass\": \"/api/v1/classes/%s\",\n" +
			"  \"user\": \"/api/v1/users/%s\"\n" +
			"}";

		return String.format(userPrivilegeJsonTemplate, schoolClass.getId(), testUser.getId());
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