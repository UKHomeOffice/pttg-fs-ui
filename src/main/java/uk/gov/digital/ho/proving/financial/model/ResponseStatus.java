package uk.gov.digital.ho.proving.financial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseStatus implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    private String code;

    @JsonCreator
    public ResponseStatus(@JsonProperty("code") String code,
                          @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ResponseStatus{" +
            "code='" + code + '\'' +
            ", message='" + message + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseStatus that = (ResponseStatus) o;
        return Objects.equals(message, that.message) &&
            Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, code);
    }
}
