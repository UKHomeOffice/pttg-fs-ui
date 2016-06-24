package uk.gov.digital.ho.proving.financial.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.format.annotation.DateTimeFormat;
import uk.gov.digital.ho.proving.financial.integration.DailyBalanceStatusResult;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class FundingCheckResponse implements Serializable {

    private final String sortCode;
    private final String accountNumber;

    private final boolean fundingRequirementMet;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate periodCheckedFrom;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate periodCheckedTo;

    private final BigDecimal minimum;


    @JsonCreator
    public FundingCheckResponse(@JsonProperty("sortCode") String sortCode,
                                @JsonProperty("accountNumber") String accountNumber,
                                @JsonProperty("fundingRequirementMet") boolean fundingRequirementMet,
                                @JsonProperty("periodCheckedFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodCheckedFrom,
                                @JsonProperty("periodCheckedTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodCheckedTo,
                                @JsonProperty("minimum") BigDecimal minimum) {

        this.sortCode = formatSortCode(sortCode);
        this.accountNumber = accountNumber;
        this.fundingRequirementMet = fundingRequirementMet;
        this.periodCheckedFrom = periodCheckedFrom;
        this.periodCheckedTo = periodCheckedTo;
        this.minimum = minimum;
    }

    public FundingCheckResponse(DailyBalanceStatusResult result) {
        this.sortCode = formatSortCode(result.getAccount().getSortCode());
        this.accountNumber = result.getAccount().getAccountNumber();
        this.fundingRequirementMet = result.isPass();
        this.periodCheckedFrom = result.getFromDate();
        this.periodCheckedTo = result.getToDate();
        this.minimum = result.getMinimum();
    }

    public String getSortCode() {
        return sortCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public boolean isFundingRequirementMet() {
        return fundingRequirementMet;
    }

    public LocalDate getPeriodCheckedFrom() {
        return periodCheckedFrom;
    }

    public LocalDate getPeriodCheckedTo() {
        return periodCheckedTo;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    @Override
    public String toString() {
        return "FundingCheckResponse{" +
            "sortCode='" + sortCode + '\'' +
            ", accountNumber='" + accountNumber + '\'' +
            ", fundingRequirementMet=" + fundingRequirementMet +
            ", periodCheckedFrom=" + periodCheckedFrom +
            ", periodCheckedTo=" + periodCheckedTo +
            ", minimum=" + minimum +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FundingCheckResponse that = (FundingCheckResponse) o;
        return fundingRequirementMet == that.fundingRequirementMet &&
            Objects.equals(sortCode, that.sortCode) &&
            Objects.equals(accountNumber, that.accountNumber) &&
            Objects.equals(periodCheckedFrom, that.periodCheckedFrom) &&
            Objects.equals(periodCheckedTo, that.periodCheckedTo) &&
            Objects.equals(minimum, that.minimum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sortCode, accountNumber, fundingRequirementMet, periodCheckedFrom, periodCheckedTo, minimum);
    }

    private String formatSortCode(String sortCode) {

        if (sortCode.length() != 6) {
            return sortCode;
        }

        StringBuilder sb = new StringBuilder(sortCode);
        sb.insert(2,'-').insert(5,'-');

        return sb.toString();
    }
}
