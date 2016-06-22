package uk.gov.digital.ho.proving.financial.exception;

/**
 * @Author Home Office Digital
 */
public class AccountNotFoundException extends RuntimeException {

    private String code;

    public AccountNotFoundException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
