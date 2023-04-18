package core.framework.web.exception;

import core.framework.exception.BaseRuntimeException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author ebin
 */
public class BaseRuntimeExceptionHandler implements ExceptionHandler {
    @Override
    public ExceptionResponse getResponseMessage(HttpServletRequest request, Exception ex) {
        if (ex instanceof BaseRuntimeException exception) {
            return responseMessage(ex.getMessage(), exception.errorCode());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean support(Exception ex) {
        return ex instanceof BaseRuntimeException;
    }
}
