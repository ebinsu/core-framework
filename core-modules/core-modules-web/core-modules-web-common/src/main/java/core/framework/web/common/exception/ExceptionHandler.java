package core.framework.web.common.exception;


import jakarta.servlet.http.HttpServletRequest;

/**
 * @author ebin
 */
public interface ExceptionHandler {
    ExceptionResponse getResponseMessage(HttpServletRequest request, Exception ex);

    boolean support(Exception ex);

    default ExceptionResponse responseMessage(String message, String errorCode) {
        return new ExceptionResponse(errorCode, message);
    }
}
