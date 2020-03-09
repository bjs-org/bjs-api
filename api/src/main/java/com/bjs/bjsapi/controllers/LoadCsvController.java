package com.bjs.bjsapi.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.context.annotation.Import;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bjs.bjsapi.config.MvcConfiguration;
import com.bjs.bjsapi.database.model.Class;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Import(MvcConfiguration.class)
@Slf4j
public class LoadCsvController {

	public final EntityLinks entityLinks;
	private final LoadCsvDataService loadCsvDataService;

	public LoadCsvController(LoadCsvDataService loadCsvDataService, EntityLinks entityLinks) {
		this.loadCsvDataService = loadCsvDataService;
		this.entityLinks = entityLinks;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/classes/upload")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile multipartFile) {
		try {
			final List<String> input = parseLines(multipartFile);
			final List<Class> classes = loadCsvDataService.loadAndSaveCsv(input);
			return ResponseEntity.ok(CollectionModel.wrap(classes));
		} catch (IOException e) {
			log.warn("Could not read multipart file", e);
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.info("Could not parse input csv, \"{}\"", e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	private List<String> parseLines(@RequestParam("file") MultipartFile multipartFile) throws IOException {
		List<String> input;
		try (Scanner scanner = new Scanner(multipartFile.getInputStream(), StandardCharsets.UTF_8.name())) {
			input = new ArrayList<>();
			while (scanner.hasNextLine()) input.add(scanner.nextLine());
		}
		return input;
	}

}
