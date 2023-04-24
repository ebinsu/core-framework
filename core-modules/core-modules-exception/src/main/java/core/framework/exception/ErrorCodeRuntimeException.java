package core.framework.exception;

/**
 * @author ebin
 */
public class ErrorCodeRuntimeException extends RuntimeException {
    private final String errorCode;

    public ErrorCodeRuntimeException(String message) {
        super(message);
        errorCode = "UNASSIGNED";
    }

    public ErrorCodeRuntimeException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCodeRuntimeException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String errorCode() {
        return errorCode;
    }
}