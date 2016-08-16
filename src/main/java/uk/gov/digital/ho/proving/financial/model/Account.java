package uk.gov.digital.ho.proving.financial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Account implements Serializable {

    @NotNull(message = "Missing parameter")
    @Pattern(regexp = "^(?!000000)\\d{6}$", message = "Invalid parameter format")
    private String sortCode;

    @NotNull(message = "Missing parameter")
    @Pattern(regexp = "^(?!00000000)\\d{8}$", message = "Invalid parameter format")
    private String accountNumber;

    @NotNull(message = "Missing parameter")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dob;

    @JsonCreator
    public Account(@JsonProperty("sortCode") String sortCode,
                   @JsonProperty("accountNumber") String accountNumber,
                   @JsonProperty("dob") LocalDate dob ) {
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.dob = dob;
    }

    // todo how to make pathvariable binding work via jsoncreator instead of setters?
    public Account() {
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return "Account{" +
            "sortCode='" + sortCode + '\'' +
            ", accountNumber='" + accountNumber + '\'' +
            ", dob='" + dob + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;

        Account account = (Account) o;

        if (sortCode != null ? !sortCode.equals(account.sortCode) : account.sortCode != null) return false;
        if (accountNumber != null ? !accountNumber.equals(account.accountNumber) : account.accountNumber != null)
            return false;
        return dob != null ? dob.equals(account.dob) : account.dob == null;

    }

    @Override
    public int hashCode() {
        int result = sortCode != null ? sortCode.hashCode() : 0;
        result = 31 * result + (accountNumber != null ? accountNumber.hashCode() : 0);
        result = 31 * result + (dob != null ? dob.hashCode() : 0);
        return result;
    }
}
