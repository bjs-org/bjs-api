package com.bjs.bjsapi.helper;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bjs.bjsapi.config.ApiConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ClassificationInformationService {

    private static final Logger log = LoggerFactory.getLogger(ClassificationInformationService.class);

    private final ApiConfiguration apiConfiguration;

    public ClassificationInformationService(ApiConfiguration apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
    }

    public int getVictoryValue(boolean female, Integer age) {
        if (age <= 19) {
            return getValue(female, true, age);
        } else {
            return getValue(female, true, age);
        }
    }

    public int getHonorValue(boolean female, Integer age) {
        if (age <= 19) {
            return getValue(female, false, age);
        } else {
            return getValue(female, false, age);
        }
    }

    int getValue(boolean female, boolean victory, Integer age) {
        URL file = apiConfiguration.getClassificationInformationFilePath();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(file);

            JsonNode firstLayer = giveFirstLayer(root, female);
            JsonNode secondLayer = giveSecondLayer(firstLayer, victory);
            JsonNode thirdLayer = giveThirdLayer(secondLayer, age);

            return thirdLayer.asInt();
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Could not parse file \"%s\"", file), e);
        }
    }

    private JsonNode giveThirdLayer(JsonNode secondLayer, Integer age) {
        return throwIfNotAccessible(secondLayer, age.toString());
    }

    private JsonNode giveFirstLayer(JsonNode root, boolean female) {
        JsonNode firstLayer;
        if (female) {
            firstLayer = throwIfNotAccessible(root, "female");
        } else {
            firstLayer = throwIfNotAccessible(root, "male");
        }
        return firstLayer;
    }

    private JsonNode giveSecondLayer(JsonNode maleFemaleInformation, boolean victory) {
        JsonNode secondLayer;
        if (victory) {
            secondLayer = throwIfNotAccessible(maleFemaleInformation, "victory");
        } else {
            secondLayer = throwIfNotAccessible(maleFemaleInformation, "honor");
        }

        return secondLayer;
    }

    private JsonNode throwIfNotAccessible(JsonNode fromJsonNode, String keyword) {
        JsonNode returnJsonNode;
        if (fromJsonNode.hasNonNull(keyword)) {
            returnJsonNode = fromJsonNode.get(keyword);
        } else {
            throw new IllegalArgumentException(String.format("The JSON file has a wrong format. Could not find property \"%s\"", keyword));
        }
        return returnJsonNode;
    }
}
