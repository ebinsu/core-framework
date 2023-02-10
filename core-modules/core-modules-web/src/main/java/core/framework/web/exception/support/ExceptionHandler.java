package core.framework.web.exception.support;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;


/**
 * @author ebin
 */
public interface ExceptionHandler {
    ExceptionResponse handleHeaderAndMessage(HttpServletResponse response, Exception ex);

    boolean support(Exception ex);

    default ExceptionResponse responseMessage(String message, String errorCode) {
        return new ExceptionResponse(errorCode, message);
    }
}
