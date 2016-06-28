package uk.gov.digital.ho.proving.financial.exception;

import org.springframework.http.HttpStatus;

/**
 * @Author Home Office Digital
 */
public class RestServiceException extends RuntimeException {

    private final HttpStatus statusCode;

    public RestServiceException(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
