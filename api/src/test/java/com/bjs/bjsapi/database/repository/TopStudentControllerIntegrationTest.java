package com.bjs.bjsapi.database.repository;

import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;

import com.bjs.bjsapi.controllers.StudentRestController;
import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.helper.ClassBuilder;
import com.bjs.bjsapi.database.model.helper.StudentBuilder;
import com.bjs.bjsapi.database.model.helper.UserPrivilegeBuilder;

public class TopStudentControllerIntegrationTest extends RepositoryIntegrationTest {

	@MockBean
	private StudentRestController studentRestController;

	private ParameterDescriptor gradeDescriptor = parameterWithName("grade").description("The grade, whose best students are searched");
	private ParameterDescriptor femaleDescriptor = parameterWithName("female").description("Classify the gender of the top 3 report");

	private List<FieldDescriptor> responseDescriptors = Arrays.asList(
		subsectionWithPath("male").description("Top 3 male students from this grade"),
		subsectionWithPath("female").description("Top 3 female students from this grade"),
		subsectionWithPath("_links").description("All links regarding this endpoint")
	);
	private List<FieldDescriptor> genderResponseDescriptors = Arrays.asList(
		subsectionWithPath("first").description("Best student from this grade and gender"),
		subsectionWithPath("second").description("Second best student from this grade and gender"),
		subsectionWithPath("third").description("Third best student from this grade and gender"),
		subsectionWithPath("_links").description("All links regarding this endpoint")
	);

	@Test
	void maleAndFemale() throws Exception {
		mvc.perform(get("/api/v1/students/best/{grade}", 7)
			.with(asAdmin()))
			.andExpect(jsonPath("male.[*].firstName").value(hasItems("Dominic", "Ayk", "Liam")))
			.andExpect(jsonPath("female.[*].firstName").value(hasItems("Anke", "Betina", "Marie")))
			.andDo(document("students-best-get",
				pathParameters(gradeDescriptor),
				responseFields(responseDescriptors)
			));
	}

	@Test
	void male() throws Exception {
		mvc.perform(get("/api/v1/students/best/{grade}?female={female}", 7, false)
			.with(asAdmin()))
			.andExpect(jsonPath("[*].firstName").value(hasItems("Dominic", "Ayk", "Liam")));
	}

	@Test
	void female() throws Exception {
		mvc.perform(get("/students/best/{grade}?female={female}", 7, true)
			.with(asAdmin()))
			.andExpect(jsonPath("[*].firstName").value(hasItems("Anke", "Betina", "Marie")))
			.andDo(document("students-best-get-female",
				pathParameters(gradeDescriptor),
				requestParameters(femaleDescriptor),
				responseFields(genderResponseDescriptors)
			));
	}

	@Test
	void security_maleAndFemale() throws Exception {
		mvc.perform(get("/api/v1/students/best/{grade}", 7)
			.with(asUser()))
			.andExpect(jsonPath("male.first").exists())
			.andExpect(jsonPath("male.second").exists())
			.andExpect(jsonPath("male.third").exists())
			.andExpect(jsonPath("female.first").exists())
			.andExpect(jsonPath("female.second").exists())
			.andExpect(jsonPath("female.third").exists())
			.andExpect(jsonPath("male.[*].firstName").value(hasItems("Dominic", "Ayk", "Liam")))
			.andExpect(jsonPath("female.[*].firstName").doesNotExist())
			.andExpect(jsonPath("female.[*]._links").value(hasSize(3)))
			.andDo(document("students-best-get",
				pathParameters(gradeDescriptor),
				responseFields(responseDescriptors)
			));
	}

	@BeforeEach
	void setUp() throws Exception {
		super.setUp();
		setupScenario();
	}

	public void setupScenario() {
		runAsAdmin(() -> {
			Class class7a = classRepository.save(new ClassBuilder().setClassName("A").setGrade("7").setClassTeacherName("Teacher").createClass());
			Class class7b = classRepository.save(new ClassBuilder().setClassName("B").setGrade("7").setClassTeacherName("Teacher").createClass());

			// 7B
			studentRepository.save(new StudentBuilder().setSchoolClass(class7b).setFirstName("Moritz").setLastName("Bushaltestellenwart").setBirthDay(Date.valueOf(LocalDate.of(2000, 1, 1))).setFemale(false).createStudent());
			studentRepository.save(new StudentBuilder().setSchoolClass(class7b).setFirstName("Casper").setLastName("Flugpilot").setBirthDay(Date.valueOf(LocalDate.of(2000, 1, 1))).setFemale(false).createStudent());
			studentRepository.save(new StudentBuilder().setSchoolClass(class7b).setFirstName("Alfred").setLastName("Taxifahrer").setBirthDay(Date.valueOf(LocalDate.of(2000, 1, 1))).setFemale(false).createStudent());

			// 7A
			studentRepository.save(new StudentBuilder().setSchoolClass(class7a).setFirstName("Liam").setLastName("Gott").setBirthDay(Date.valueOf(LocalDate.of(2000, 1, 1))).setFemale(false).createStudent());
			studentRepository.save(new StudentBuilder().setSchoolClass(class7a).setFirstName("Ayk").setLastName("Teufel").setBirthDay(Date.valueOf(LocalDate.of(2000, 1, 1))).setFemale(false).createStudent());
			studentRepository.save(new StudentBuilder().setSchoolClass(class7a).setFirstName("Dominic").setLastName("Arzt").setBirthDay(Date.valueOf(LocalDate.of(2000, 1, 1))).setFemale(false).createStudent());

			// 7A
			studentRepository.save(new StudentBuilder().setSchoolClass(class7a).setFirstName("Anja").setLastName("Chefin").setBirthDay(Date.valueOf(LocalDate.of(2000, 1, 1))).setFemale(true).createStudent());
			studentRepository.save(new StudentBuilder().setSchoolClass(class7a).setFirstName("Simone").setLastName("Mauerer").setBirthDay(Date.valueOf(LocalDate.of(2000, 1, 1))).setFemale(true).createStudent());
			studentRepository.save(new StudentBuilder().setSchoolClass(class7a).setFirstName("Nicole").setLastName("Arbeitslose").setBirthDay(Date.valueOf(LocalDate.of(2000, 1, 1))).setFemale(true).createStudent());

			// 7B
			studentRepository.save(new StudentBuilder().setSchoolClass(class7b).setFirstName("Anke").setLastName("Chirog").setBirthDay(Date.valueOf(LocalDate.of(2000, 1, 1))).setFemale(true).createStudent());
			studentRepository.save(new StudentBuilder().setSchoolClass(class7b).setFirstName("Betina").setLastName("Notfallarzt").setBirthDay(Date.valueOf(LocalDate.of(2000, 1, 1))).setFemale(true).createStudent());
			studentRepository.save(new StudentBuilder().setSchoolClass(class7b).setFirstName("Marie").setLastName("Lastkraftfahrerin").setBirthDay(Date.valueOf(LocalDate.of(2000, 1, 1))).setFemale(true).createStudent());

			when(studentRestController.calculateScore(any())).thenAnswer(invocation -> invocation.<Student>getArgument(0).getId().intValue() * 100);

			userPrivilegeRepository.save(new UserPrivilegeBuilder().setAccessibleClass(class7a).setUser(user).createUserPrivilege());
		});
	}

}
