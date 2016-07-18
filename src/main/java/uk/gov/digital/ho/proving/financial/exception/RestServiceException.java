package uk.gov.digital.ho.proving.financial.exception;

import org.springframework.http.HttpStatus;

/**
 * @Author Home Office Digital
 */
public class RestServiceException extends RuntimeException {

    private final HttpStatus statusCode;
    private final String reasonMessage;
    private final String reasonCode;



    public RestServiceException(HttpStatus statusCode, String reasonMessage, String reasonCode) {
        this.statusCode = statusCode;
        this.reasonMessage = reasonMessage;
        this.reasonCode = reasonCode;
    }

    public RestServiceException(HttpStatus statusCode) {
        this(statusCode, statusCode.getReasonPhrase(), "");
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getReasonMessage() {
        return reasonMessage;
    }

    public String getReasonCode() {
        return reasonCode;
    }
}
