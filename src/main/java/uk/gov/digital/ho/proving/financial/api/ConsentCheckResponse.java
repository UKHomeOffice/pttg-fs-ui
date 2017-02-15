package uk.gov.digital.ho.proving.financial.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @Author Home Office Digital
 */
public class ConsentCheckResponse implements Serializable {

    private static final long serialVersionUID = 2846845302466820382L;

    private final String consent;
    private final ConsentStatus status;

    @JsonCreator
    public ConsentCheckResponse(@JsonProperty("consent") String consent,
                                @JsonProperty("status") ConsentStatus status) {
        this.consent = consent;
        this.status = status;
    }

    public String getConsent() {
        return consent;
    }

    public ConsentStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConsentCheckResponse that = (ConsentCheckResponse) o;

        if (consent != null ? !consent.equals(that.consent) : that.consent != null) return false;
        return status != null ? status.equals(that.status) : that.status == null;
    }

    @Override
    public int hashCode() {
        int result = consent != null ? consent.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ConsentCheckResponse{" +
            "consent='" + consent + '\'' +
            ", status=" + status +
            '}';
    }
}
