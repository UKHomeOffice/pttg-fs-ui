package uk.gov.digital.ho.proving.financial.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConditionCodesResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String applicantConditionCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String partnerConditionCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String childConditionCode;

    @JsonCreator
    public ConditionCodesResponse(@JsonProperty("applicantConditionCode") String applicantConditionCode,
                                  @JsonProperty("partnerConditionCode") String partnerConditionCode,
                                  @JsonProperty("childConditionCode") String childConditionCode) {
        this.applicantConditionCode = applicantConditionCode;
        this.partnerConditionCode = partnerConditionCode;
        this.childConditionCode = childConditionCode;
    }

    public String getApplicantConditionCode() {
        return applicantConditionCode;
    }

    public String getPartnerConditionCode() {
        return partnerConditionCode;
    }

    public String getChildConditionCode() {
        return childConditionCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConditionCodesResponse that = (ConditionCodesResponse) o;

        if (applicantConditionCode != null ? !applicantConditionCode.equals(that.applicantConditionCode) : that.applicantConditionCode != null)
            return false;
        if (partnerConditionCode != null ? !partnerConditionCode.equals(that.partnerConditionCode) : that.partnerConditionCode != null)
            return false;
        return childConditionCode != null ? childConditionCode.equals(that.childConditionCode) : that.childConditionCode == null;
    }

    @Override
    public int hashCode() {
        int result = applicantConditionCode != null ? applicantConditionCode.hashCode() : 0;
        result = 31 * result + (partnerConditionCode != null ? partnerConditionCode.hashCode() : 0);
        result = 31 * result + (childConditionCode != null ? childConditionCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ConditionCodesResponse{" +
            "applicantConditionCode='" + applicantConditionCode + '\'' +
            ", partnerConditionCode='" + partnerConditionCode + '\'' +
            ", childConditionCode='" + childConditionCode + '\'' +
            '}';
    }
}
