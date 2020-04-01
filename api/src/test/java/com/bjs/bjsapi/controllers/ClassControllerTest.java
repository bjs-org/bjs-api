package com.bjs.bjsapi.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.repository.ClassRepository;
import com.bjs.bjsapi.security.BJSUserDetailsService;

@WebMvcTest(ClassController.class)
class ClassControllerTest {

	private final Class sampleClass = Class
		.builder()
		.className("A")
		.id(5L)
		.grade("7")
		.build();

	@MockBean
	private BJSUserDetailsService userDetailsService;

	@MockBean
	private ClassRepository classRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void accessible() throws Exception {
		mockMvc
			.perform(get("/api/v2/classes"))
			.andExpect(status().isOk());

		mockMvc
			.perform(get("/api/v2/classes/"))
			.andExpect(status().isOk());
	}

	@Test
	void returnsClasses() throws Exception {
		final List<Class> classes = Collections.singletonList(sampleClass);

		when(classRepository.findAll()).thenReturn(classes);

		mockMvc
			.perform(get("/api/v2/classes/"))
			.andExpect(status().isOk())
			.andExpect(content().json("{\n" +
				"  \"classes\": [\n" +
				"    {\n" +
				"      \"id\": 5,\n" +
				"      \"grade\": \"7\",\n" +
				"      \"className\": \"A\"\n" +
				"    " +
				"}\n" +
				"  ]\n" +
				"}"));
	}

	@Test
	void returnsClass() throws Exception {
		when(classRepository.findById(sampleClass.getId())).thenReturn(Optional.of(sampleClass));

		mockMvc
			.perform(get("/api/v2/classes/{id}", sampleClass.getId()))
			.andExpect(status().isOk())
			.andExpect(content().json("{\n" +
				"  \"id\": 5,\n" +
				"  \"grade\": \"7\",\n" +
				"  \"className\": \"A\"\n" +
				"}"));
	}

	@Test
	void returnsNotFoundWhenNotFound() throws Exception {
		when(classRepository.findById(sampleClass.getId())).thenReturn(Optional.of(sampleClass));

		mockMvc
			.perform(get("/api/v2/classes/{id}", 6L))
			.andExpect(status().isNotFound());
	}

}