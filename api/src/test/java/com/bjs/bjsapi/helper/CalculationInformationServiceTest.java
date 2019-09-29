package com.bjs.bjsapi.helper;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.bjs.bjsapi.config.CalculationInformationConfig;
import com.bjs.bjsapi.database.model.enums.DisciplineType;

@RunWith(SpringRunner.class)
public class CalculationInformationServiceTest {

	@MockBean
	private CalculationInformationConfig calculationInformationConfig;

	private CalculationInformationService informationService;

	@Before
	public void setUp() {
		Path path = getResourceFromClassPath("calculation_information_test.json");

		doReturn(path).when(calculationInformationConfig).getCalculationInformationFilePath();
		informationService = new CalculationInformationService(calculationInformationConfig);
	}

	private Path getResourceFromClassPath(String name) {
		ClassLoader classLoader = getClass().getClassLoader();
		URL url = Objects.requireNonNull(classLoader.getResource(name));
		return new File(url.getFile()).toPath();
	}

	@Test
	public void test_throwsIllegalArgumentException_wrongFormat() {
		reset(calculationInformationConfig);
		doReturn(getResourceFromClassPath("calculation_information_wrong_format.json")).when(calculationInformationConfig).getCalculationInformationFilePath();

		Throwable thrown = catchThrowable(() -> informationService.getValue(true, true, DisciplineType.RUN_50));

		assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
			.hasNoCause()
			.withFailMessage("The JSON file has a wrong format. Could not find property \"female\"");
	}

	@Test
	public void test_throwsIllegalArgumentException_couldNotParseFile() {
		reset(calculationInformationConfig);
		doReturn(Paths.get("fileWhichNotExists.json")).when(calculationInformationConfig).getCalculationInformationFilePath();

		Throwable thrown = catchThrowable(() -> informationService.getValue(true, true, DisciplineType.RUN_50));

		assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
			.hasCauseInstanceOf(FileNotFoundException.class)
			.withFailMessage("Could not parse file \"fileWhichNotExists.json\"");
	}

	@Test
	public void test_getValue_female_a_RUN_50() {
		assertThat(informationService.getValue(true, true, DisciplineType.RUN_50)).isEqualTo(3.64800);
		assertThat(informationService.getAValue(true, DisciplineType.RUN_50)).isEqualTo(3.64800);
	}

	@Test
	public void test_getValue_female_c_RUN_50() {
		assertThat(informationService.getValue(true, false, DisciplineType.RUN_50)).isEqualTo(0.00660);
		assertThat(informationService.getCValue(true, DisciplineType.RUN_50)).isEqualTo(0.00660);
	}

	@Test
	public void test_getValue_male_a_RUN_50() {
		assertThat(informationService.getValue(false, true, DisciplineType.RUN_50)).isEqualTo(3.79000);
		assertThat(informationService.getAValue(false, DisciplineType.RUN_50)).isEqualTo(3.79000);
	}

	@Test
	public void test_getValue_male_c_RUN_50() {
		assertThat(informationService.getValue(false, false, DisciplineType.RUN_50)).isEqualTo(0.00690);
		assertThat(informationService.getCValue(false, DisciplineType.RUN_50)).isEqualTo(0.00690);
	}

}