package com.bjs.bjsapi.database.repository;

import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;

class ClassRepositoryIntegrationTest extends RepositoryIntegrationTest {

	private final ParameterDescriptor idDescriptor = parameterWithName("id").description("The class' ID");
	private final ParameterDescriptor classNameDescriptor = parameterWithName("className").description("The class' name");
	private final ParameterDescriptor classTeacherNameDescriptor = parameterWithName("classTeacherName").description("The class teacher's name");

	public static final List<FieldDescriptor> CLASS_RESPONSE = Arrays.asList(
		fieldWithPath("grade").description("The class' grade").type(JsonFieldType.STRING),
		fieldWithPath("className").description("The class' name").type(JsonFieldType.STRING),
		fieldWithPath("classTeacherName").description("The class teacher's name").type(JsonFieldType.STRING).optional(),
		subsectionWithPath("_links").description("Links regarding this class")
	);
	public static final List<FieldDescriptor> CLASSES_RESPONSE = Arrays.asList(
		subsectionWithPath("_links").description("All links regarding classes"),
		fieldWithPath("_embedded.classes[]").description("All (visible) classes")
	);
	public static final List<FieldDescriptor> CLASS_REQUEST = Arrays.asList(
		fieldWithPath("grade").description("The class' grade").type(JsonFieldType.STRING),
		fieldWithPath("className").description("The class' name").type(JsonFieldType.STRING),
		fieldWithPath("classTeacherName").description("The class' teacher").optional().type(JsonFieldType.STRING)
	);
	public static final List<FieldDescriptor> CLASS_REQUEST_OPTIONAL = Arrays.asList(
		fieldWithPath("grade").description("The class' grade").optional().type(JsonFieldType.STRING),
		fieldWithPath("className").description("The class' name").optional().type(JsonFieldType.STRING),
		fieldWithPath("classTeacherName").description("The class teacher's name").optional().type(JsonFieldType.STRING)
	);

	@BeforeEach
	void setUp() throws Exception {
		super.setUp();
		runAsAdmin(() -> {
			testData.setupClasses();
		});
	}

	@Test
	void test_findAll_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/classes"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_findAll_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/classes")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$._embedded.classes.[*]", hasSize(1)))
			.andExpect(jsonPath("$._embedded.classes.[0].className").value(testData.accessibleClass.getClassName()))
			.andDo(document("classes-get-all",
				responseFields(CLASSES_RESPONSE).andWithPrefix("_embedded.classes[].", CLASS_RESPONSE)
			));
	}

	@Test
	void test_findAll_admin() throws Exception {
		mvc.perform(get("/api/v1/classes")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$._embedded.classes.[*]", hasSize(2)))
			.andExpect(jsonPath("$._embedded.classes.[*].className", containsInAnyOrder(testData.accessibleClass.getClassName(), testData.inaccessibleClass.getClassName())));
	}

	@Test
	void test_findById_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/classes/{id}", testData.inaccessibleClass.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findById_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/classes/{id}", testData.accessibleClass.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.classTeacherName").value(testData.accessibleClass.getClassTeacherName()))
			.andExpect(jsonPath("$.className").value(testData.accessibleClass.getClassName()))
			.andDo(document("classes-get-byId",
				pathParameters(idDescriptor),
				responseFields(CLASS_RESPONSE)
			));
	}

	@Test
	void test_findById_admin() throws Exception {
		mvc.perform(get("/api/v1/classes/{id}", testData.accessibleClass.getId())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		mvc.perform(get("/api/v1/classes/{id}", testData.inaccessibleClass.getId())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	void test_findByName_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", testData.inaccessibleClass.getClassName())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findByName_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", testData.accessibleClass.getClassName())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("classes-get-byName",
				requestParameters(classNameDescriptor),
				responseFields(CLASS_RESPONSE)
			));
	}

	@Test
	void test_findByName_admin_allData() throws Exception {
		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", testData.accessibleClass.getClassName())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.className").value(testData.accessibleClass.getClassName()));

		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", testData.inaccessibleClass.getClassName())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.className").value(testData.inaccessibleClass.getClassName()));
	}

	@Test
	void test_findByClassTeacher_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/classes/search/findByClassTeacherName?classTeacherName={classTeacherName}", testData.accessibleClass.getClassTeacherName())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._embedded.classes.[*]", hasSize(1)))
			.andExpect(jsonPath("$._embedded.classes.[*].className", hasItem(testData.accessibleClass.getClassName())))
			.andDo(document("classes-get-byTeacher",
				requestParameters(classTeacherNameDescriptor),
				responseFields(CLASSES_RESPONSE).andWithPrefix("_embedded.classes[].", CLASS_RESPONSE)
			));
	}

	@Test
	void test_findByClassTeacher_admin() throws Exception {
		mvc.perform(get("/api/v1/classes/search/findByClassTeacherName?classTeacherName={classTeacherName}", testData.inaccessibleClass.getClassTeacherName())
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._embedded.classes.[*]", hasSize(2)))
			.andExpect(jsonPath("$._embedded.classes.[*].className", hasItems(testData.accessibleClass.getClassName(), testData.inaccessibleClass.getClassName())));
	}

	@Test
	void test_create_unauthenticated() throws Exception {
		mvc.perform(post("/api/v1/classes")
			.with(anonymous())
			.content(IntegrationTestData.giveNewClass("A", "A Class Teacher", "7"))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_create_unauthorized() throws Exception {
		// only admins can create new classes
		mvc.perform(post("/api/v1/classes")
			.with(asUser())
			.content(IntegrationTestData.giveNewClass("A", "A Class Teacher", "7"))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_create_admin() throws Exception {
		mvc.perform(post("/api/v1/classes")
			.with(asAdmin())
			.content(IntegrationTestData.giveNewClass("A", "ABC", "7"))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(document("classes-post",
				requestFields(CLASS_REQUEST),
				responseFields(CLASS_RESPONSE)
			))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("className").value("A"))
			.andExpect(jsonPath("classTeacherName").value("ABC"));
	}

	@Test
	void test_delete_unauthenticated() throws Exception {
		mvc.perform(delete("/api/v1/classes/{id}", testData.inaccessibleClass.getId())
			.with(anonymous()))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_delete_unauthorized() throws Exception {
		// only admins can delete classes
		mvc.perform(delete("/api/v1/classes/{id}", testData.accessibleClass.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());

		mvc.perform(delete("/api/v1/classes/{id}", testData.inaccessibleClass.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_delete_admin() throws Exception {
		mvc.perform(delete("/api/v1/classes/{id}", testData.accessibleClass.getId())
			.with(asAdmin()))
			.andDo(document("classes-delete",
				pathParameters(idDescriptor)))
			.andExpect(status().isNoContent());

		mvc.perform(get("/api/v1/classes/{id}", testData.accessibleClass.getId())
			.with(asAdmin()))
			.andExpect(status().isNotFound());

		mvc.perform(delete("/api/v1/classes/{id}", testData.inaccessibleClass.getId())
			.with(asAdmin()))
			.andExpect(status().isNoContent());
	}

	@Test
	void test_edit_unauthorized() throws Exception {
		// only those classes which are in your privilege list are editable
		mvc.perform(patch("/api/v1/classes/{id}", testData.inaccessibleClass.getId())
			.content(IntegrationTestData.giveNewClass("A", "ABC", "7"))
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_edit_authorized() throws Exception {
		mvc.perform(patch("/api/v1/classes/{id}", testData.accessibleClass.getId())
			.content(IntegrationTestData.giveNewClass("A", "ABC", "7"))
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andDo(document("classes-patch",
				requestFields(CLASS_REQUEST_OPTIONAL),
				pathParameters(idDescriptor),
				responseFields(CLASS_RESPONSE)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("className").value("A"))
			.andExpect(jsonPath("classTeacherName").value("ABC"));
	}

	@Test
	void test_edit_admin() throws Exception {
		mvc.perform(patch("/api/v1/classes/{id}", testData.accessibleClass.getId())
			.content(IntegrationTestData.giveNewClass("A", "ABC", "7"))
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("className").value("A"))
			.andExpect(jsonPath("classTeacherName").value("ABC"));
	}

	@Test
	void test_replace_unauthorized() throws Exception {
		// only those classes which are in your privilege list are replaceable
		mvc.perform(put("/api/v1/classes/{id}", testData.inaccessibleClass.getId())
			.content(IntegrationTestData.giveNewClass("A", "ABC", "7"))
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_replace_authorized() throws Exception {
		mvc.perform(put("/api/v1/classes/{id}", testData.accessibleClass.getId())
			.content(IntegrationTestData.giveNewClass("A", "ABC", "7"))
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andDo(document("classes-put",
				requestFields(CLASS_REQUEST),
				pathParameters(idDescriptor)
			))
			.andExpect(status().isOk())
			.andExpect(jsonPath("className").value("A"));
	}

	@Test
	void test_replace_admin() throws Exception {
		mvc.perform(put("/api/v1/classes/{id}", testData.accessibleClass.getId())
			.content(IntegrationTestData.giveNewClass("A", "ABC", "7"))
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("className").value("A"));
	}

}
