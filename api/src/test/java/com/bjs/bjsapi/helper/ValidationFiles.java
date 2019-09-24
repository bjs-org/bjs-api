package com.bjs.bjsapi.helper;

import static java.nio.charset.StandardCharsets.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.api.Assertions;

public class ValidationFiles {

	private static final Path TEST_OUTPUT_DATA_DIR = Paths.get("data/test/output");
	private static final Path TEST_VALIDATION_DATA_DIR = Paths.get("data/test/validation");
	public static final String MASK = "[MASK]";

	public static void checkWithValidationFile(String filename, String content) throws IOException {

		Path validationFile = TEST_VALIDATION_DATA_DIR.resolve(filename);
		Path outputFile = TEST_OUTPUT_DATA_DIR.resolve(filename);

		Path outputFolder = outputFile.getParent();
		if (!Files.exists(outputFolder)) {
			Files.createDirectories(outputFolder);
		}

		writeToFile(outputFile, content);

		createValidationFileIfNotExists(validationFile, content);

		Assertions.assertThat(outputFile).hasSameContentAs(validationFile);

	}

	public static void createValidationFileIfNotExists(Path validationFile) throws IOException {
		createValidationFileIfNotExists(validationFile, "");
	}

	public static void createValidationFileIfNotExists(Path validationFile, String content) throws IOException {
		String filename = validationFile.getFileName().toString();
		Files.createDirectories(validationFile.getParent());
		if (!Files.exists(validationFile)) {
			writeToFile(validationFile, "=== new file \"" + filename + "\" ===\n" + content);
		}
	}

	public static void writeToFile(Path path, String string) throws IOException {
		path.getParent().toFile().mkdirs();
		Files.write(path, string.getBytes(UTF_8));
	}

	public static String mask(String input, Object... toMaskObjects) {
		for (Object toMaskObject : toMaskObjects) {
			String toMask;
			if (toMaskObject instanceof String) {
				toMask = (String) toMaskObject;
			} else {
				toMask = String.valueOf(toMaskObject);
			}

			if (toMask != null) {
				input = input.replaceAll(toMask, MASK);
			}
		}

		return input;
	}

}
