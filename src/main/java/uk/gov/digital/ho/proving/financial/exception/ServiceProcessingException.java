package uk.gov.digital.ho.proving.financial.exception;

/**
 * @Author Home Office Digital
 */
public class ServiceProcessingException extends RuntimeException {

    private String code;

    public ServiceProcessingException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
