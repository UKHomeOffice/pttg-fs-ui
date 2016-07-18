package uk.gov.digital.ho.proving.financial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;


public final class ResponseDetails {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String message;

    private final String code;

    public static ResponseDetails notFoundResponseDetails(){
        return new ResponseDetails("404", "not found");
    }

    @JsonCreator
    public ResponseDetails(@JsonProperty("code") String code, @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseDetails(ErrorCode error) {
        this.code = error.getCode();
        this.message = error.getMessage();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    @Override
    public String toString() {
        return "ResponseDetails{" +
            "code='" + code + '\'' +
            ", message='" + message + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseDetails that = (ResponseDetails) o;
        return Objects.equals(code, that.code) &&
            Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }
}
