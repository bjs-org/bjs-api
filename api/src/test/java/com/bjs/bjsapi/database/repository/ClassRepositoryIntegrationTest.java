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

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.helper.ClassBuilder;
import com.bjs.bjsapi.database.model.helper.UserPrivilegeBuilder;

class ClassRepositoryIntegrationTest extends RepositoryIntegrationTest {

	private Class accessibleClass;
	private Class inaccessibleClass;
	private final ParameterDescriptor idDescriptor = parameterWithName("id").description("The class' ID");
	private final ParameterDescriptor classNameDescriptor = parameterWithName("className").description("The class' name");
	private final ParameterDescriptor classTeacherNameDescriptor = parameterWithName("classTeacherName").description("The class teacher's name");

	private final List<FieldDescriptor> classesResponse = Arrays.asList(
		subsectionWithPath("_links").description("All links regarding classes"),
		fieldWithPath("_embedded.classes[]").description("All (visible) classes")
	);
	private final List<FieldDescriptor> classResponse = Arrays.asList(
		fieldWithPath("className").description("The class' name").type(JsonFieldType.STRING),
		fieldWithPath("grade").description("The grade this class belongs to").type(JsonFieldType.STRING),
		fieldWithPath("classTeacherName").description("The class teacher's name").type(JsonFieldType.STRING),
		subsectionWithPath("_links").description("Links regarding this class")
	);
	private final List<FieldDescriptor> classRequest = Arrays.asList(
		fieldWithPath("className").description("The class' name").type(JsonFieldType.STRING),
		fieldWithPath("grade").description("The grade this class belongs to").type(JsonFieldType.STRING),
		fieldWithPath("classTeacherName").description("The class' teacher").optional().type(JsonFieldType.STRING)
	);
	private final List<FieldDescriptor> classRequestOptional = Arrays.asList(
		fieldWithPath("className").description("The class' name").optional().type(JsonFieldType.STRING),
		fieldWithPath("grade").description("The grade this class belongs to").optional().type(JsonFieldType.STRING),
		fieldWithPath("classTeacherName").description("The class teacher's name").optional().type(JsonFieldType.STRING)
	);

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
	void test_findAll_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/classes")
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$._embedded.classes.[*]", hasSize(1)))
			.andExpect(jsonPath("$._embedded.classes.[0].className").value("A"))
			.andDo(document("classes-get-all",
				responseFields(classesResponse).andWithPrefix("_embedded.classes[].", classResponse)
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
			.andExpect(jsonPath("$._embedded.classes.[*].className", containsInAnyOrder("A", "B")));
	}

	@Test
	void test_findById_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/classes/{id}", inaccessibleClass.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findById_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/classes/{id}", accessibleClass.getId())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.classTeacherName").value("ClassTeacher"))
			.andExpect(jsonPath("$.className").value("A"))
			.andDo(document("classes-get-byId",
				pathParameters(idDescriptor),
				responseFields(classResponse)
			));
	}

	@Test
	void test_findById_admin() throws Exception {
		mvc.perform(get("/api/v1/classes/{id}", accessibleClass.getId())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		mvc.perform(get("/api/v1/classes/{id}", inaccessibleClass.getId())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	void test_findByName_unauthorized() throws Exception {
		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", inaccessibleClass.getClassName())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_findByName_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", accessibleClass.getClassName())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("classes-get-byName",
				requestParameters(classNameDescriptor),
				responseFields(classResponse)
			));
	}

	@Test
	void test_findByName_admin_allData() throws Exception {
		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", accessibleClass.getClassName())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.className").value("A"));

		mvc.perform(get("/api/v1/classes/search/findByClassName?className={className}", inaccessibleClass.getClassName())
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.className").value("B"));
	}

	@Test
	void test_findByClassTeacher_userAuthorized() throws Exception {
		mvc.perform(get("/api/v1/classes/search/findByClassTeacherName?classTeacherName={classTeacherName}", accessibleClass.getClassTeacherName())
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._embedded.classes.[*]", hasSize(1)))
			.andExpect(jsonPath("$._embedded.classes.[*].className", hasItem("A")))
			.andDo(document("classes-get-byTeacher",
				requestParameters(classTeacherNameDescriptor),
				responseFields(classesResponse).andWithPrefix("_embedded.classes[].", classResponse)
			));
	}

	@Test
	void test_findByClassTeacher_admin() throws Exception {
		mvc.perform(get("/api/v1/classes/search/findByClassTeacherName?classTeacherName={classTeacherName}", inaccessibleClass.getClassTeacherName())
			.accept(MediaType.APPLICATION_JSON)
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._embedded.classes.[*]", hasSize(2)))
			.andExpect(jsonPath("$._embedded.classes.[*].className", hasItems("A", "B")));
	}

	@Test
	void test_findByGrade_authorized() throws Exception {
		mvc.perform(get("/api/v1/classes/search/findByGrade?grade={grade}", "7")
			.with(asUser()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.classes.[*]", hasSize(1)))
			.andExpect(jsonPath("_embedded.classes.[*].className", hasItem("A")))
			.andExpect(jsonPath("_embedded.classes.[*].grade", hasItem("7")));
	}

	@Test
	void test_findByGrade_admin() throws Exception {
		mvc.perform(get("/api/v1/classes/search/findByGrade?grade={grade}", "7")
			.with(asAdmin()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.classes.[*]", hasSize(2)))
			.andExpect(jsonPath("_embedded.classes.[*].className", hasItems("A", "B")))
			.andExpect(jsonPath("_embedded.classes.[*].grade", hasItems("7", "7")));
	}

	@Test
	void test_create_unauthenticated() throws Exception {
		mvc.perform(post("/api/v1/classes")
			.with(anonymous())
			.content(givenNewClass("A", "7", "A Class Teacher"))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_create_unauthorized() throws Exception {
		// only admins can create new classes

		mvc.perform(post("/api/v1/classes")
			.with(asUser())
			.content(givenNewClass("A", "7", "A Class Teacher"))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_create_admin() throws Exception {
		final String className = "A";
		final String classTeacherName = "A Class Teacher";
		mvc.perform(post("/api/v1/classes")
			.with(asAdmin())
			.content(givenNewClass(className, "7", classTeacherName))
			.accept(MediaType.APPLICATION_JSON))
			.andDo(document("classes-post",
				requestFields(classRequest),
				responseFields(classResponse)
			))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("className").value(className))
			.andExpect(jsonPath("classTeacherName").value(classTeacherName));
	}

	@Test
	void test_delete_unauthenticated() throws Exception {
		mvc.perform(delete("/api/v1/classes/{id}", inaccessibleClass.getId())
			.with(anonymous()))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void test_delete_unauthorized() throws Exception {
		// only admins can delete classes

		mvc.perform(delete("/api/v1/classes/{id}", accessibleClass.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());

		mvc.perform(delete("/api/v1/classes/{id}", inaccessibleClass.getId())
			.with(asUser()))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_delete_admin() throws Exception {
		mvc.perform(delete("/api/v1/classes/{id}", accessibleClass.getId())
			.with(asAdmin()))
			.andDo(document("classes-delete",
				pathParameters(idDescriptor)))
			.andExpect(status().isNoContent());

		mvc.perform(delete("/api/v1/classes/{id}", inaccessibleClass.getId())
			.with(asAdmin()))
			.andExpect(status().isNoContent());
	}

	@Test
	void test_edit_unauthorized() throws Exception {
		// only those classes which are in your privilege list are editable

		final String newClassName = "changed name";
		final String newClassTeacherName = "new Class Teacher";

		mvc.perform(patch("/api/v1/classes/{id}", inaccessibleClass.getId())
			.content(givenNewClass(newClassName, "7", newClassTeacherName))
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_edit_authorized() throws Exception {
		final String newClassName = "changed name";
		final String newClassTeacherName = "new Class Teacher";

		mvc.perform(patch("/api/v1/classes/{id}", accessibleClass.getId())
			.content(givenNewClass(newClassName, "7", newClassTeacherName))
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andDo(document("classes-patch",
				requestFields(classRequestOptional),
				pathParameters(idDescriptor),
				responseFields(classResponse)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("className").value(newClassName))
			.andExpect(jsonPath("classTeacherName").value(newClassTeacherName));
	}

	@Test
	void test_edit_admin() throws Exception {
		final String newClassName = "changed name";
		final String newClassTeacherName = "new Class Teacher";

		mvc.perform(patch("/api/v1/classes/{id}", accessibleClass.getId())
			.content(givenNewClass(newClassName, "7", newClassTeacherName))
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("className").value(newClassName))
			.andExpect(jsonPath("classTeacherName").value(newClassTeacherName));
	}

	@Test
	void test_replace_unauthorized() throws Exception {
		// only those classes which are in your privilege list are replaceable

		final String newClassName = "changed name";
		final String newClassTeacherName = "";

		mvc.perform(put("/api/v1/classes/{id}", inaccessibleClass.getId())
			.content(givenNewClass(newClassName, "7", newClassTeacherName))
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void test_replace_authorized() throws Exception {
		final String newClassName = "changed name";
		final String newClassTeacherName = "";

		mvc.perform(put("/api/v1/classes/{id}", accessibleClass.getId())
			.content(givenNewClass(newClassName, "7", newClassTeacherName))
			.with(asUser())
			.accept(MediaType.APPLICATION_JSON))
			.andDo(document("classes-put",
				requestFields(classRequest),
				pathParameters(idDescriptor)
			))
			.andExpect(status().isOk())
			.andExpect(jsonPath("className").value(newClassName));
	}

	@Test
	void test_replace_admin() throws Exception {
		final String newClassName = "changed name";
		final String newClassTeacherName = "";

		mvc.perform(put("/api/v1/classes/{id}", accessibleClass.getId())
			.content(givenNewClass(newClassName, "7", newClassTeacherName))
			.with(asAdmin())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("className").value(newClassName));
	}

	private String givenNewClass(String className, String grade, String classTeacherName) {
		//language=JSON
		String template = "{\n" +
			"  \"grade\": \"%s\",\n" +
			"  \"className\": \"%s\",\n" +
			"  \"classTeacherName\": \"%s\"\n" +
			"}";

		return String.format(template, grade, className, classTeacherName);
	}

	private void setupClassScenario() {
		runAsAdmin(() -> {
			accessibleClass = classRepository.save(new ClassBuilder().setClassName("A").setGrade("7").setClassTeacherName("ClassTeacher").createClass());
			inaccessibleClass = classRepository.save(new ClassBuilder().setClassName("B").setGrade("7").setClassTeacherName("ClassTeacher").createClass());

			userPrivilegeRepository.save(new UserPrivilegeBuilder().setUser(user).setAccessibleClass(accessibleClass).createUserPrivilege());
		});
	}

}
