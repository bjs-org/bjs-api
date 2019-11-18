package com.bjs.bjsapi.database.repository;

import static com.bjs.bjsapi.helper.ValidationFiles.*;
import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.helper.ClassBuilder;
import com.bjs.bjsapi.database.model.helper.UserPrivilegeBuilder;

public class ClassRepositoryIntegrationTest extends RepositoryIntegrationTest {

	private Class privilegedClass;
	private Class unprivilegedClass;
	private final FieldDescriptor[] schoolClass = new FieldDescriptor[] {
		fieldWithPath("className").type(JsonFieldType.STRING).description("The class' name"),
		fieldWithPath("classTeacherName").type(JsonFieldType.STRING).description("The class teacher's name"),
		subsectionWithPath("_links").description("Links regarding this class")
	};
	private final ResponseFieldsSnippet schoolClasses = responseFields(
		subsectionWithPath("_links").description("All links regarding classes"),
		fieldWithPath("_embedded.classes[]").description("All (visible) classes")
	).andWithPrefix("_embedded.classes[].", schoolClass);

	@BeforeEach
	void setUp() throws Exception {
		super.setUp();
		setupClassScenario();
	}

	@Test
	void test_findAll_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/classes"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_findAll_authorized_onlyPrivilegedData() throws Exception {
		MockHttpServletResponse response = mvc.perform(get("/api/v1/classes")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("classes-get-all", schoolClasses))
			.andReturn().getResponse();

		checkWithValidationFile("web/classes-findAll-authorized-onlyPrivileged", mask(response.getContentAsString(), privilegedClass.getId().toString()));
	}

	@Test
	void test_findAll_admin_allData() throws Exception {
		MockHttpServletResponse response = mvc.perform(get("/api/v1/classes")
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		checkWithValidationFile("web/classes-findAll-admin-allData", mask(response.getContentAsString(), unprivilegedClass.getId().toString(), privilegedClass.getId().toString()));
	}

	@Test
	void test_findById_authorized_onlyPrivilegedData() throws Exception {

		mvc.perform(get("/api/v1/classes/{id}", unprivilegedClass.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());

		mvc.perform(get("/api/v1/classes/{id}", privilegedClass.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("classes-get-byId",
				pathParameters(
					parameterWithName("id").description("The class' id you want to get")
				),
				responseFields(schoolClass)));
	}

	@Test
	void test_findById_admin_allData() throws Exception {

		mvc.perform(get("/api/v1/classes/{id}", privilegedClass.getId())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		mvc.perform(get("/api/v1/classes/{id}", unprivilegedClass.getId())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	void test_findByName_authorized_onlyPrivilegedData() throws Exception {

		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", unprivilegedClass.getClassName())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());

		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", privilegedClass.getClassName())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("classes-get-byName",
				requestParameters(
					parameterWithName("className").description("The class' name you want to get")
				),
				responseFields(schoolClass)));
	}

	@Test
	void test_findByName_admin_allData() throws Exception {

		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", privilegedClass.getClassName())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", unprivilegedClass.getClassName())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	void test_findByClassTeacher_authorized_onlyPrivilegedData() throws Exception {

		MockHttpServletResponse response = mvc.perform(get("/api/v1/classes/search/findByClassTeacherName?classTeacherName={classTeacherName}", privilegedClass.getClassTeacherName())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("classes-get-byTeacher",
				requestParameters(
					parameterWithName("classTeacherName").description("The name of the teacher of the class")
				), schoolClasses))
			.andReturn().getResponse();

		checkWithValidationFile("web/classes-findByClassTeacher-authorized-onlyPrivileged", mask(response.getContentAsString(), privilegedClass.getId().toString(), unprivilegedClass.getId().toString()));
	}

	@Test
	void test_findByClassTeacher_admin_allData() throws Exception {

		String response = mvc.perform(get("/api/v1/classes/search/findByClassTeacherName?classTeacherName={classTeacherName}", unprivilegedClass.getClassTeacherName())
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse().getContentAsString();

		checkWithValidationFile("web/classes-findByClassTeacher-admin-allData", mask(response, unprivilegedClass.getId(), privilegedClass.getId()));
	}

	@Test
	void test_save_unauthorized() throws Exception {
		Class aClass = new ClassBuilder().setClassName("7A").createClass();
		aClass.setClassTeacherName("A Class Teacher");

		mvc.perform(post("/api/v1/classes")
			.content(asJsonString(aClass))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_save_admin() throws Exception {
		Class aClass = new ClassBuilder().setClassName("7A").createClass();
		aClass.setClassTeacherName("A Class Teacher");

		mvc.perform(post("/api/v1/classes")
			.with(asAdmin())
			.content(asJsonString(aClass))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(document("classes-post",
				requestFields(
					fieldWithPath("id").optional().type(JsonFieldType.NUMBER).description("The class' id"),
					fieldWithPath("className").description("The class' name"),
					fieldWithPath("classTeacherName").optional().description("The class' teacher")
				)))
			.andExpect(status().isCreated());

	}

	@Test
	void test_deleteClass_unauthorized() throws Exception {
		mvc.perform(delete("/api/v1/classes/{id}", unprivilegedClass.getId())
			.with(anonymous()))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_deleteClass_authorized_privilegedOnly() throws Exception {
		mvc.perform(delete("/api/v1/classes/{id}", privilegedClass.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());

		mvc.perform(delete("/api/v1/classes/{id}", unprivilegedClass.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_deleteClass_admin_allowed() throws Exception {
		mvc.perform(delete("/api/v1/classes/{id}", privilegedClass.getId())
			.with(asAdmin()))
			.andDo(document("classes-delete",
				pathParameters(
					parameterWithName("id").description("The class's id")
				)))
			.andExpect(status().isNoContent());

		mvc.perform(delete("/api/v1/classes/{id}", unprivilegedClass.getId())
			.with(asAdmin()))
			.andExpect(status().isNoContent());
	}

	@Test
	void test_editClass_authorized() throws Exception {
		String json = "{\n" +
			"  \"className\": \"changed name\",\n" +
			"  \"classTeacherName\": \"new Class Teacher\"\n" +
			"}";

		mvc.perform(patch("/api/v1/classes/{id}", privilegedClass.getId())
			.content(json)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andDo(document("classes-patch",
				requestFields(
					fieldWithPath("className").description("The class' name").optional(),
					fieldWithPath("classTeacherName").description("The class teacher's name").optional(),
					fieldWithPath("id").type(JsonFieldType.NUMBER).description("The class' id").optional()
				),
				pathParameters(
					parameterWithName("id").description("The class' id")
				),
				responseFields(schoolClass)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("className").value("changed name"))
			.andExpect(jsonPath("classTeacherName").value("new Class Teacher"));
	}

	@Test
	void test_putClass() throws Exception {
		String json = "{\n" +
			"  \"className\": \"changed name\"\n" +
			"}";

		mvc.perform(put("/api/v1/classes/{id}", privilegedClass.getId())
			.content(json)
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andDo(document("classes-put",
				requestFields(
					fieldWithPath("className").description("The class' name"),
					fieldWithPath("classTeacherName").type(JsonFieldType.STRING).description("The class teacher's name").optional(),
					fieldWithPath("id").type(JsonFieldType.NUMBER).description("The class' id").optional()
				),
				pathParameters(
					parameterWithName("id").description("The class' id")
				)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("className").value("changed name"));
	}

	private void setupClassScenario() {
		runAsAdmin(() -> {
			privilegedClass = new ClassBuilder().setClassName("privilegedClass").createClass();
			privilegedClass.setClassTeacherName("ClassTeacher");
			classRepository.save(privilegedClass);

			unprivilegedClass = new ClassBuilder().setClassName("unprivilegedClass").createClass();
			unprivilegedClass.setClassTeacherName("ClassTeacher");
			classRepository.save(unprivilegedClass);

			userPrivilegeRepository.save(new UserPrivilegeBuilder().setUser(user).setAccessibleClass(privilegedClass).createUserPrivilege());
		});
	}

}
