package uk.gov.digital.ho.proving.financial.model;

/**
 * @Author Home Office Digital
 */
public enum ErrorCode {

    MISSING_PARAMETER("0001", "Missing parameter: "),
    INVALID_PARAMETER_TYPE("0002", "Invalid parameter: "),
    INVALID_PARAMETER_FORMAT("0003", "Invalid parameter: "),
    INVALID_PARAMETER_VALUE("0004", "Invalid parameter: "),
    INTERNAL_ERROR("0005", "Internal server error"),
    API_SERVER_ERROR("0006", "Error at FSS API server"),
    API_CLIENT_ERROR("0007", "Bad request to FSS API server"),
    ;

    private String code;
    private String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
