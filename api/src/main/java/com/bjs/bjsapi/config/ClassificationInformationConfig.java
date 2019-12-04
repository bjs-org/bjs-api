package com.bjs.bjsapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URL;

@Configuration
@ConfigurationProperties(prefix = "api.calculation")
public class ClassificationInformationConfig {

    private static final Logger log = LoggerFactory.getLogger(ClassificationInformationConfig.class);

    private String classificationInformationFile = "classification_information.json";

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