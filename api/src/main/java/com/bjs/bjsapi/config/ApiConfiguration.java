package com.bjs.bjsapi.config;

import java.io.IOException;
import java.net.URL;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@ConfigurationProperties(prefix = "api")
@Slf4j
public class ApiConfiguration {
	private String calculationInformationFile = "calculation_information.json";
	private String classificationInformationFile = "classification_information.json";

	public String getCalculationInformationFile() {
		return calculationInformationFile;
	}

	public URL getCalculationInformationFilePath() {
		try {
			return new ClassPathResource(calculationInformationFile).getURL();
		} catch (IOException e) {
			log.error("Error occurred while finding path of calculation-information-file.", e);
			return null;
		}
	}

	public void setCalculationInformationFile(String calculationInformationFile) {
		this.calculationInformationFile = calculationInformationFile;
	}

	public String getClassificationInformationFile() {
		return classificationInformationFile;
	}

	public URL getClassificationInformationFilePath() {
		try {
			return new ClassPathResource(classificationInformationFile).getURL();
		} catch (IOException e) {
			log.error("Error occurred while finding path of classification-information-file.", e);
			return null;
		}
	}

	public void setClassificationInformationFile(String calculationInformationFile) {
		this.classificationInformationFile = classificationInformationFile;
	}

}
