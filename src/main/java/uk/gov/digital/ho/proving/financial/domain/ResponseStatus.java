package uk.gov.digital.ho.proving.financial.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ResponseStatus {
    private String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;


    public ResponseStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
