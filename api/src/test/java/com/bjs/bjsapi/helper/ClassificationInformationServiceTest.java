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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bjs.bjsapi.config.ApiConfiguration;

@ExtendWith(SpringExtension.class)
public class ClassificationInformationServiceTest {

    @MockBean
    private ApiConfiguration apiConfiguration;

    private ClassificationInformationService informationService;

    @BeforeEach
    public void setUp() throws IOException {
        doReturn(getResourceFromClassPath("classification_information_test.json")).when(apiConfiguration).getClassificationInformationFilePath();
        informationService = new ClassificationInformationService(apiConfiguration);
    }

    private URL getResourceFromClassPath(String name) throws IOException {
        return new ClassPathResource(name).getURL();
    }

    @Test
    public void test_throwsIllegalArgumentException_wrongFormat() throws IOException {
        reset(apiConfiguration);
        doReturn(getResourceFromClassPath("calculation_information_wrong_format.json")).when(apiConfiguration).getClassificationInformationFilePath();

        Throwable thrown = catchThrowable(() -> informationService.getValue(true, true, 17));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
            .hasNoCause()
            .withFailMessage("The JSON file has a wrong format. Could not find property \"female\"");
    }

    @Test
    public void test_throwsIllegalArgumentException_couldNotParseFile() throws MalformedURLException {
        reset(apiConfiguration);
        doReturn(Paths.get("fileWhichNotExists.json").toUri().toURL()).when(apiConfiguration).getClassificationInformationFilePath();

        Throwable thrown = catchThrowable(() -> informationService.getValue(true, true, 17));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
            .hasCauseInstanceOf(FileNotFoundException.class)
            .withFailMessage("Could not parse file \"fileWhichNotExists.json\"");
    }

    @Test
    void test_getValue_edgeCases_7() {
        assertThat(informationService.getVictoryValue(true, 7)).isEqualTo(475);
        assertThat(informationService.getHonorValue(true, 7)).isEqualTo(625);
    }

    @Test
    void test_getValue_edgeCases_20() {
        assertThat(informationService.getVictoryValue(true, 20)).isEqualTo(950);
        assertThat(informationService.getHonorValue(true, 20)).isEqualTo(1150);
    }

    @Test
    public void test_getValue_female_victory_17() {
        assertThat(informationService.getValue(true, true, 17)).isEqualTo(925);
        assertThat(informationService.getVictoryValue(true, 17)).isEqualTo(925);
    }

    @Test
    public void test_getValue_female_honor_17() {
        assertThat(informationService.getValue(true, false, 17)).isEqualTo(1125);
        assertThat(informationService.getHonorValue(true, 17)).isEqualTo(1125);
    }

    @Test
    public void test_getValue_male_victory_17() {
        assertThat(informationService.getValue(false, true, 17)).isEqualTo(1125);
        assertThat(informationService.getVictoryValue(false, 17)).isEqualTo(1125);
    }

    @Test
    public void test_getValue_male_honor_17() {
        assertThat(informationService.getValue(false, false, 17)).isEqualTo(1400);
        assertThat(informationService.getHonorValue(false, 17)).isEqualTo(1400);
    }

}