package uk.gov.digital.ho.proving.financial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.financial.integration.RestServiceErrorHandler;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * @Author Home Office Digital
 */
@Configuration
@EnableRetry
@ComponentScan("uk.gov.digital.ho.proving.financial")
@PropertySource(value = "classpath:dsp-default.properties")
@PropertySource(value = "classpath:/developer/developer-default.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:/developer/${user.name}-default.properties", ignoreResourceNotFound = true)
public class ServiceConfiguration {

    @Autowired
    private Environment environment;

    @Value("${connectionAttemptCount}")
    private int connectionAttemptCount;

    @Value("${connectionRetryDelay}")
    private int connectionRetryDelay;

    @Autowired
    private RestServiceErrorHandler errorHandler;

    @Bean
    public ObjectMapper getMapper() {
        ObjectMapper m = new ObjectMapper();
        m.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        m.enable(SerializationFeature.INDENT_OUTPUT);
        return m;
    }

    @Bean
    public RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());

        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.setErrorHandler(errorHandler);

        return restTemplate;
    }

    @Bean
    @ConfigurationProperties(prefix = "rest.connection")
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory();
    }


    @Bean(name = "connectionExceptionInterceptor")
    public RetryOperationsInterceptor getConnectionExceptionInterceptor() {

        HashMap<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();

        retryableExceptions.put(ConnectException.class, true);
        retryableExceptions.put(InterruptedIOException.class, true);

        SimpleRetryPolicy retry = new SimpleRetryPolicy(connectionAttemptCount, retryableExceptions, true);

        FixedBackOffPolicy backOff = new FixedBackOffPolicy();
        backOff.setBackOffPeriod(connectionRetryDelay);

        return RetryInterceptorBuilder.stateless()
            .retryPolicy(retry)
            .backOffPolicy(backOff)
            .build();
    }
}
