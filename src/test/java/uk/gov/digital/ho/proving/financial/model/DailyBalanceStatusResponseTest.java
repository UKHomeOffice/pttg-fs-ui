package uk.gov.digital.ho.proving.financial.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Author Home Office Digital
 */
public class DailyBalanceStatusResponseTest {

    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception{
        mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    public void shouldSerializeToJson() throws Exception {

        DailyBalanceStatusResponse sample = new DailyBalanceStatusResponse(
            anAccount("112233", "12345678"),
            aDailyBalanceCheck(LocalDate.of(2015, 01, 30), 1, true),
            aResponseStatus("200", "OK"));

        String actual = jsonFrom(sample);

        String expected = stringFrom("/daily-balance-check-response.json");

        assertThat(actual.replace(" ", "")).isEqualTo(expected.replace(" ", ""));
    }

    @Test
    public void shouldDeserializeFromJson() throws Exception {

        DailyBalanceStatusResponse expected = new DailyBalanceStatusResponse(
            anAccount("112233", "12345678"),
            aDailyBalanceCheck(LocalDate.of(2015, 01, 30), 1, true),
            aResponseStatus("200", "OK"));

        DailyBalanceStatusResponse actual = objectFrom("/daily-balance-check-response.json");

        assertThat(actual).isEqualTo(expected);
    }

    private Account anAccount(String sortCode, String accountNumber) {
        return new Account(sortCode, accountNumber);
    }

    private DailyBalanceCheck aDailyBalanceCheck(LocalDate aDate, int threshold, boolean minimumAboveThreshold) {
        return new DailyBalanceCheck(aDate, aDate.minusDays(27), BigDecimal.valueOf(threshold), minimumAboveThreshold);
    }

    private ResponseStatus aResponseStatus(String code, String message) {
        return new ResponseStatus(code, message);
    }

    private DailyBalanceStatusResponse objectFrom(String file) throws Exception {

        InputStream input = this.getClass().getResourceAsStream(file);

        return mapper.readValue(input, DailyBalanceStatusResponse.class);
    }

    private String stringFrom(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get(this.getClass().getResource(file).toURI()))).trim();
    }

    private String jsonFrom(DailyBalanceStatusResponse sample) throws JsonProcessingException {
        return mapper.writeValueAsString(sample);
    }
}
