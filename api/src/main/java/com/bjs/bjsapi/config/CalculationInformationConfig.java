package com.bjs.bjsapi.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api.calculation")
public class CalculationInformationConfig {

	private String calculationInformationFile;

	public String getCalculationInformationFile() {
		return calculationInformationFile;
	}

	public Path getCalculationInformationFilePath() {
		return Paths.get(calculationInformationFile);
	}

	public void setCalculationInformationFile(String calculationInformationFile) {
		this.calculationInformationFile = calculationInformationFile;
	}

}
