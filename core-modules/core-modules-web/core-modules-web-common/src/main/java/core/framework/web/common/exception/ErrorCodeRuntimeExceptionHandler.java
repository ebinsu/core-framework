package core.framework.web.common.exception;

import core.framework.exception.ErrorCodeRuntimeException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author ebin
 */
public class ErrorCodeRuntimeExceptionHandler implements ExceptionHandler {
    @Override
    public ExceptionResponse getResponseMessage(HttpServletRequest request, Exception ex) {
        if (ex instanceof ErrorCodeRuntimeException exception) {
            return responseMessage(ex.getMessage(), exception.errorCode());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean support(Exception ex) {
        return ex instanceof ErrorCodeRuntimeException;
    }
}
