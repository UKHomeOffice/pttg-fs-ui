package uk.gov.digital.ho.proving.financial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;

/**
 * @Author Home Office Digital
 */
@Configuration
public class ServiceConfiguration {

    @Bean
    public ObjectMapper getMapper() {
        ObjectMapper m = new ObjectMapper();
        m.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        m.enable(SerializationFeature.INDENT_OUTPUT);
        return m;
    }
}
