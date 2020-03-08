package com.bjs.bjsapi.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class LoadCsvController {

	private final LoadCsvDataService loadCsvDataService;

	public LoadCsvController(LoadCsvDataService loadCsvDataService) {
		this.loadCsvDataService = loadCsvDataService;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/api/v1/classes/upload")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile multipartFile) {
		try (Scanner scanner = new Scanner(multipartFile.getInputStream(), StandardCharsets.UTF_8.name())) {
			List<String> input = new ArrayList<>();
			while (scanner.hasNextLine()) input.add(scanner.nextLine());
			loadCsvDataService.loadAndSaveCsv(input);

			return ResponseEntity.ok().build();
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
