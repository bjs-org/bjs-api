package com.bjs.bjsapi.helper;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.bjs.bjsapi.config.ApiConfiguration;
import com.bjs.bjsapi.database.model.enums.DisciplineType;

@SpringJUnitConfig
class CalculationInformationServiceTest {

	@MockBean
	private ApiConfiguration apiConfiguration;

	private CalculationInformationService informationService;

	@BeforeEach
	void setUp() throws IOException {
		doReturn(getResourceFromClassPath("calculation_information_test.json")).when(apiConfiguration).getCalculationInformationFilePath();
		informationService = new CalculationInformationService(apiConfiguration);
	}

	private URL getResourceFromClassPath(String name) throws IOException {
		return new ClassPathResource(name).getURL();
	}

	@Test
	void test_throwsIllegalArgumentException_wrongFormat() throws IOException {
		reset(apiConfiguration);
		doReturn(getResourceFromClassPath("calculation_information_wrong_format.json")).when(apiConfiguration).getCalculationInformationFilePath();

		Throwable thrown = catchThrowable(() -> informationService.getValue(true, true, DisciplineType.RUN_50));

		assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
			.hasNoCause()
			.withFailMessage("The JSON file has a wrong format. Could not find property \"female\"");
	}

	@Test
	void test_throwsIllegalArgumentException_couldNotParseFile() throws MalformedURLException {
		reset(apiConfiguration);
		doReturn(Paths.get("fileWhichNotExists.json").toUri().toURL()).when(apiConfiguration).getCalculationInformationFilePath();

		Throwable thrown = catchThrowable(() -> informationService.getValue(true, true, DisciplineType.RUN_50));

		assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
			.hasCauseInstanceOf(FileNotFoundException.class)
			.withFailMessage("Could not parse file \"fileWhichNotExists.json\"");
	}

	@Test
	void test_getValue_female_a_RUN_50() {
		assertThat(informationService.getValue(true, true, DisciplineType.RUN_50)).isEqualTo(3.64800);
		assertThat(informationService.getAValue(true, DisciplineType.RUN_50)).isEqualTo(3.64800);
	}

	@Test
	void test_getValue_female_c_RUN_50() {
		assertThat(informationService.getValue(true, false, DisciplineType.RUN_50)).isEqualTo(0.00660);
		assertThat(informationService.getCValue(true, DisciplineType.RUN_50)).isEqualTo(0.00660);
	}

	@Test
	void test_getValue_male_a_RUN_50() {
		assertThat(informationService.getValue(false, true, DisciplineType.RUN_50)).isEqualTo(3.79000);
		assertThat(informationService.getAValue(false, DisciplineType.RUN_50)).isEqualTo(3.79000);
	}

	@Test
	void test_getValue_male_c_RUN_50() {
		assertThat(informationService.getValue(false, false, DisciplineType.RUN_50)).isEqualTo(0.00690);
		assertThat(informationService.getCValue(false, DisciplineType.RUN_50)).isEqualTo(0.00690);
	}

}