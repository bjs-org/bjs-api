package com.bjs.bjsapi.config;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@ConfigurationProperties(prefix = "api.calculation")
public class CalculationInformationConfig {

	private static final Logger log = LoggerFactory.getLogger(CalculationInformationConfig.class);

	private String calculationInformationFile = "calculation_information.json";

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

}
