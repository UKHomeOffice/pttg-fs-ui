package uk.gov.digital.ho.proving.financial.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @Author Home Office Digital
 */
public class ConsentStatus implements Serializable {

    private static final long serialVersionUID = 8942517347514305451L;

    private final String code;
    private final String message;

    @JsonCreator
    public ConsentStatus(@JsonProperty("code") String code,
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConsentStatus that = (ConsentStatus) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ConsentStatus{" +
            "code='" + code + '\'' +
            ", message='" + message + '\'' +
            '}';
    }
}
