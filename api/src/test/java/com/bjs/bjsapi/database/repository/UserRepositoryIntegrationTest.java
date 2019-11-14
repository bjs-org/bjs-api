package com.bjs.bjsapi.database.repository;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultHandler;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.helper.UserBuilder;
import com.bjs.bjsapi.helper.SecurityHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserRepositoryIntegrationTest extends RepositoryIntegrationTest {

	private User firstUser;
	private User secondUser;

	private JacksonTester<User> jacksonTester;

	private final Logger log = LoggerFactory.getLogger(UserRepositoryIntegrationTest.class);
	private final ResultHandler PRINT_HANDLER = result -> log.info(result.getResponse().getContentAsString());

	private User newUser;

	private List<FieldDescriptor> userResponse = Arrays.asList(
		fieldWithPath("username").description("The user's username"),
		fieldWithPath("password").description("The encrypted password"),
		fieldWithPath("administrator").description("If the user is a administrator"),
		fieldWithPath("enabled").description("If the user is enabled and can login"),
		subsectionWithPath("_links").description("All links regarding this user")
	);
	private List<FieldDescriptor> userRequestOptional = Arrays.asList(
		fieldWithPath("id").description("The user's id").optional().type(JsonFieldType.NUMBER),
		fieldWithPath("username").description("The user's username").optional().type(JsonFieldType.STRING),
		fieldWithPath("password").description("The user's password in plain text").optional().type(JsonFieldType.STRING),
		fieldWithPath("enabled").description("If this account should be enabled").optional().type(JsonFieldType.BOOLEAN),
		fieldWithPath("administrator").description("Defines whether is user should get administrator rights").optional().type(JsonFieldType.BOOLEAN)
	);
	private List<FieldDescriptor> userRequest = Arrays.asList(
		fieldWithPath("id").description("The user's id").optional().type(JsonFieldType.NUMBER),
		fieldWithPath("username").description("The user's username"),
		fieldWithPath("password").description("The user's password in plain text"),
		fieldWithPath("administrator").description("If the user is a administrator").optional().type(JsonFieldType.BOOLEAN),
		fieldWithPath("enabled").description("If the user is enabled and can login").optional().type(JsonFieldType.BOOLEAN)
	);

	@Override
	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
		JacksonTester.initFields(this, objectMapper);
		setupUserScenario();
		SecurityHelper.reset();
	}

	@Test
	public void test_findAll_unauthenticated() throws Exception {
		mvc.perform(get("/api/v1/users")
			.with(anonymous())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void test_findAll_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/users")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void test_findAll_admin() throws Exception {
		mvc.perform(get("/api/v1/users")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._embedded.users").isArray())
			.andExpect(jsonPath("$._embedded.users[*].username", hasItems(firstUser.getUsername(), secondUser.getUsername())))
			.andDo(document("users-get-all",
				responseFields(
					subsectionWithPath("_links").description("All links regarding users"),
					subsectionWithPath("_embedded.users").description("All users")
				).andWithPrefix("_embedded.users[].", userResponse)))
			.andDo(PRINT_HANDLER);

	}

	@Test
	public void test_findById_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/users/{id}", firstUser.getId())
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	public void test_findById_admin() throws Exception {
		mvc.perform(get("/api/v1/users/{id}", firstUser.getId())
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username", is(firstUser.getUsername())))
			.andExpect(jsonPath("$._links.self.href", containsString("/api/v1/users/")))
			.andDo(document("users-get-byId",
				pathParameters(
					parameterWithName("id").description("The user's id")
				), responseFields(userResponse)))
			.andDo(PRINT_HANDLER);
	}

	@Test
	public void test_findByUsername_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/users/search/findByUsername?username={username}", firstUser.getUsername())
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	public void test_findByUsername_admin() throws Exception {
		mvc.perform(get("/api/v1/users/search/findByUsername?username={username}", firstUser.getUsername())
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username", is(firstUser.getUsername())))
			.andExpect(jsonPath("$._links.self.href", containsString("/api/v1/users/")))
			.andDo(document("users-get-byUsername",
				requestParameters(
					parameterWithName("username").description("The user's username")
				),
				responseFields(userResponse))
			)
			.andDo(PRINT_HANDLER);
	}

	@Test
	public void test_create_unauthorized() throws Exception {
		mvc.perform(post("/api/v1/users")
			.content(givenNewUser())
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	public void test_create_admin() throws Exception {
		mvc.perform(post("/api/v1/users")
			.content(givenNewUser())
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isCreated())
			.andDo(document("users-post",
				requestFields(userRequest),
				responseFields(userResponse)
			));
	}

	@Test
	public void test_edit_unauthorized() throws Exception {
		mvc.perform(patch("/api/v1/users/{id}", firstUser.getId())
			.content("{\n" +
				"  \"enabled\": false\n" +
				"}")
			.accept(MediaType.APPLICATION_JSON)
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	public void test_edit_admin() throws Exception {
		mvc.perform(patch("/api/v1/users/{id}", firstUser.getId())
			.content("{\n" +
				"  \"enabled\": false\n" +
				"}")
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.enabled", is(false)))
			.andDo(document("users-patch", pathParameters(
				parameterWithName("id").description("The user's id")
				),
				requestFields(userRequestOptional),
				responseFields(userResponse))
			);
	}

	@Test
	public void test_replace_unauthorized() throws Exception {
		mvc.perform(put("/api/v1/users/{id}", firstUser.getId())
			.content(givenNewUser())
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	public void test_replace_admin() throws Exception {
		mvc.perform(put("/api/v1/users/{id}", firstUser.getId())
			.accept(MediaType.APPLICATION_JSON)
			.content(givenNewUser())
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andDo(PRINT_HANDLER)
			.andDo(document("users-put", pathParameters(
				parameterWithName("id").description("The user's id")
				),
				requestFields(userRequest),
				responseFields(userResponse)
			));
	}

	@Test
	public void test_delete_unauthorized() throws Exception {
		mvc.perform(delete("/api/v1/users/{id}", firstUser.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	public void test_delete_admin() throws Exception {
		mvc.perform(delete("/api/v1/users/{id}", firstUser.getId())
			.with(asAdmin()))
			.andExpect(status().isNoContent())
			.andDo(document("users-delete", pathParameters(
				parameterWithName("id").description("The user's id")
			)));
	}

	private void setupUserScenario() {
		SecurityHelper.runAs("admin", "admin", "ROLE_USER", "ROLE_ADMIN");

		firstUser = userRepository.save(new UserBuilder()
			.setUsername("firstUser")
			.setPassword("new Password")
			.createUser());

		secondUser = userRepository.save(new UserBuilder()
			.setUsername("secondUser")
			.setPassword("other Password")
			.createUser());

		SecurityHelper.reset();
	}

	private String givenNewUser() throws IOException {
		newUser = new UserBuilder()
			.setUsername("aNewUser")
			.setPassword("123456")
			.createUser();

		ObjectNode node = (ObjectNode) objectMapper.readTree(jacksonTester.write(newUser).getJson());
		node.remove("id");

		return node.toString();
	}

}
