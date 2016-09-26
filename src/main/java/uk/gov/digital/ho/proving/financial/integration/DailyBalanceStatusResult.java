package uk.gov.digital.ho.proving.financial.integration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import uk.gov.digital.ho.proving.financial.model.FailureReason;
import uk.gov.digital.ho.proving.financial.model.ResponseDetails;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DailyBalanceStatusResult implements Serializable {

    private boolean pass;

    private BigDecimal minimum;

    private String accountHolderName;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate fromDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private FailureReason failureReason;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final ResponseDetails status;

    @JsonCreator
    public DailyBalanceStatusResult(@JsonProperty("accountHolderName") String name,
                                    @JsonProperty("pass") boolean pass,
                                    @JsonProperty("failureReason") FailureReason failureReason,
                                    @JsonProperty("status") ResponseDetails status) {
        this.accountHolderName=name;
        this.pass = pass;
        this.failureReason = failureReason;
        this.status = status;
    }

    public DailyBalanceStatusResult withMinimum(BigDecimal minimum) {
        this.minimum = minimum;
        return this;
    }

    public DailyBalanceStatusResult withFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public boolean isPass() {
        return pass;
    }

    public ResponseDetails getStatus() {
        return status;
    }

    public FailureReason getFailureReason() {
        return failureReason;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    @Override
    public String toString() {
        return "DailyBalanceStatusResult{" +
            "pass=" + pass +
            ", minimum=" + minimum +
            ", accountHolderName='" + accountHolderName + '\'' +
            ", fromDate=" + fromDate +
            ", failureReason=" + failureReason +
            ", status=" + status +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyBalanceStatusResult)) return false;

        DailyBalanceStatusResult that = (DailyBalanceStatusResult) o;

        if (pass != that.pass) return false;
        if (minimum != null ? !minimum.equals(that.minimum) : that.minimum != null) return false;
        if (accountHolderName != null ? !accountHolderName.equals(that.accountHolderName) : that.accountHolderName != null)
            return false;
        if (fromDate != null ? !fromDate.equals(that.fromDate) : that.fromDate != null) return false;
        if (failureReason != null ? !failureReason.equals(that.failureReason) : that.failureReason != null)
            return false;
        return status != null ? status.equals(that.status) : that.status == null;

    }

    @Override
    public int hashCode() {
        int result = (pass ? 1 : 0);
        result = 31 * result + (minimum != null ? minimum.hashCode() : 0);
        result = 31 * result + (accountHolderName != null ? accountHolderName.hashCode() : 0);
        result = 31 * result + (fromDate != null ? fromDate.hashCode() : 0);
        result = 31 * result + (failureReason != null ? failureReason.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
