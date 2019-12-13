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

import com.bjs.bjsapi.helper.SecurityHelper;
import com.bjs.bjsapi.security.helper.RunWithAuthentication;

class UserPrivilegeRepositoryIntegrationTest extends RepositoryIntegrationTest {

	private final Logger log = LoggerFactory.getLogger(UserPrivilegeRepositoryIntegrationTest.class);

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
	private final ParameterDescriptor idDescriptor = parameterWithName("id").description("The ID of the user privilege");

	@Override
	@BeforeEach
	void setUp() throws Exception {
		super.setUp();
		RunWithAuthentication.runAsAdmin(() -> {
			testData.setupClasses();
		});
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
			.andExpect(jsonPath("$._links.profile.href").value(endsWith("/api/v1/profile/user_privileges")))
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
		mvc.perform(get("/api/v1/user_privileges/{id}", testData.accessClassPrivilege.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findById_admin() throws Exception {
		mvc.perform(get("/api/v1/user_privileges/{id}", testData.accessClassPrivilege.getId())
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
		mvc.perform(get("/api/v1/user_privileges/search/findByAccessibleClass?accessibleClass={accessibleClass}", "/" + testData.accessibleClass.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findByAccessibleClass_admin() throws Exception {
		mvc.perform(get("/api/v1/user_privileges/search/findByAccessibleClass?accessibleClass=api/v1/classes/" + testData.accessibleClass.getId())
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
		mvc.perform(get("/api/v1/user_privileges/search/findByUser?user=api/v1/users/" + testData.user.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findByUser_admin() throws Exception {
		mvc.perform(get("/api/v1/user_privileges/search/findByUser?user=api/v1/users/" + testData.user.getId())
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
		mvc.perform(patch("/api/v1/user_privileges/{id}", testData.accessClassPrivilege.getId())
			.content(givenChangedClassUserPrivilege())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_edit_admin() throws Exception {
		mvc.perform(patch("/api/v1/user_privileges/{id}", testData.accessClassPrivilege.getId())
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

	@Test
	void test_replace_unauthorized() throws Exception {
		mvc.perform(put("/api/v1/user_privileges/{id}", testData.accessClassPrivilege.getId())
			.with(asUser())
			.content(givenNewUserPrivilege())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_replace_admin() throws Exception {
		mvc.perform(put("/api/v1/user_privileges/{id}", testData.accessClassPrivilege.getId())
			.with(asAdmin())
			.content(givenNewUserPrivilege())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._links.accessibleClass.href").isString())
			.andExpect(jsonPath("$._links.user.href").isString())
			.andDo(document("user-privileges-put",
				pathParameters(idDescriptor),
				responseFields(userPrivilegeResponse),
				requestFields(userPrivilegeRequest)
			));
	}


	@Test
	void test_delete_unauthorized() throws Exception {
		mvc.perform(delete("/api/v1/user_privileges/{id}", testData.accessClassPrivilege.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_delete_admin() throws Exception {
		mvc.perform(delete("/api/v1/user_privileges/{id}", testData.accessClassPrivilege.getId())
			.with(asAdmin()))
			.andExpect(status().isNoContent())
			.andDo(document("user-privileges-delete",
				pathParameters(idDescriptor)
			));
	}

	private String givenChangedClassUserPrivilege() {
		//language=JSON
		String userPrivilegeJsonTemplate = "{\n" +
			"  \"accessibleClass\": \"/api/v1/classes/%s\"\n" +
			"}";

		return String.format(userPrivilegeJsonTemplate, testData.accessibleClass.getId());
	}

	private String givenNewUserPrivilege() {
		//language=JSON
		String userPrivilegeJsonTemplate = "{\n" +
			"  \"accessibleClass\": \"/api/v1/classes/%s\",\n" +
			"  \"user\": \"/api/v1/users/%s\"\n" +
			"}";

		return String.format(userPrivilegeJsonTemplate, testData.accessibleClass.getId(), testData.user.getId());
	}

}