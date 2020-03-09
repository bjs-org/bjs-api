package com.bjs.bjsapi.helper;

import java.io.IOException;
import java.net.URL;

import org.springframework.stereotype.Service;

import com.bjs.bjsapi.config.ApiConfiguration;
import com.bjs.bjsapi.database.model.enums.DisciplineType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CalculationInformationService {

	private final ApiConfiguration apiConfiguration;

	public CalculationInformationService(ApiConfiguration apiConfiguration) {
		this.apiConfiguration = apiConfiguration;
	}

	public double getAValue(boolean female, DisciplineType discipline) {
		return getValue(female, true, discipline);
	}

	public double getCValue(boolean female, DisciplineType discipline) {
		return getValue(female, false, discipline);
	}

	double getValue(boolean female, boolean a, DisciplineType discipline) {
		URL file = apiConfiguration.getCalculationInformationFilePath();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode root = objectMapper.readTree(file);

			JsonNode firstLayer = giveFirstLayer(root, female);
			JsonNode secondLayer = giveSecondLayer(firstLayer, a);
			JsonNode thirdLayer = giveThirdLayer(secondLayer, discipline);

			return thirdLayer.asDouble();
		} catch (IOException e) {
			throw new IllegalArgumentException(String.format("Could not parse file \"%s\"", file), e);
		}
	}

	private JsonNode giveThirdLayer(JsonNode secondLayer, DisciplineType discipline) {
		return throwIfNotAccessible(secondLayer, discipline.toString());
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

	private JsonNode giveSecondLayer(JsonNode maleFemaleInformation, boolean a) {
		JsonNode secondLayer;
		if (a) {
			secondLayer = throwIfNotAccessible(maleFemaleInformation, "a");
		} else {
			secondLayer = throwIfNotAccessible(maleFemaleInformation, "c");
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
