package uk.gov.digital.ho.proving.financial.integration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import uk.gov.digital.ho.proving.financial.model.ResponseDetails;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DailyBalanceStatusResult implements Serializable {

    private boolean pass;
    private BigDecimal minimum;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate fromDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final ResponseDetails status;

    @JsonCreator
    public DailyBalanceStatusResult(@JsonProperty("pass") boolean pass,
                                    @JsonProperty("status") ResponseDetails status) {
        this.pass = pass;
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

    @Override
    public String toString() {
        return "DailyBalanceStatusResult{" +
            "pass=" + pass +
            ", minimum=" + minimum +
            ", fromDate=" + fromDate +
            ", status=" + status +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyBalanceStatusResult that = (DailyBalanceStatusResult) o;
        return pass == that.pass &&
            Objects.equals(minimum, that.minimum) &&
            Objects.equals(fromDate, that.fromDate) &&
            Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pass, minimum, fromDate, status);
    }
}
