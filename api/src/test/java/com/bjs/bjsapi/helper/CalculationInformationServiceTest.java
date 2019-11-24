package com.bjs.bjsapi.helper;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import com.bjs.bjsapi.config.CalculationInformationConfig;
import com.bjs.bjsapi.database.model.enums.DisciplineType;

@RunWith(SpringRunner.class)
public class CalculationInformationServiceTest {

	@MockBean
	private CalculationInformationConfig calculationInformationConfig;

	private CalculationInformationService informationService;

	@Before
	public void setUp() throws IOException {
		doReturn(getResourceFromClassPath("calculation_information_test.json")).when(calculationInformationConfig).getCalculationInformationFilePath();
		informationService = new CalculationInformationService(calculationInformationConfig);
	}

	private URL getResourceFromClassPath(String name) throws IOException {
		return new ClassPathResource(name).getURL();
	}

	@Test
	public void test_throwsIllegalArgumentException_wrongFormat() throws IOException {
		reset(calculationInformationConfig);
		doReturn(getResourceFromClassPath("calculation_information_wrong_format.json")).when(calculationInformationConfig).getCalculationInformationFilePath();

		Throwable thrown = catchThrowable(() -> informationService.getValue(true, true, DisciplineType.RUN_50));

		assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
			.hasNoCause()
			.withFailMessage("The JSON file has a wrong format. Could not find property \"female\"");
	}

	@Test
	public void test_throwsIllegalArgumentException_couldNotParseFile() throws MalformedURLException {
		reset(calculationInformationConfig);
		doReturn(Paths.get("fileWhichNotExists.json").toUri().toURL()).when(calculationInformationConfig).getCalculationInformationFilePath();

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