package uk.gov.digital.ho.proving.financial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;

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

    @JsonCreator
    public Account(@JsonProperty("sortCode") String sortCode,
                   @JsonProperty("accountNumber") String accountNumber) {
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
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

    @Override
    public String toString() {
        return "Account{" +
            "sortCode='" + sortCode + '\'' +
            ", accountNumber='" + accountNumber + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(sortCode, account.sortCode) &&
            Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sortCode, accountNumber);
    }
}
