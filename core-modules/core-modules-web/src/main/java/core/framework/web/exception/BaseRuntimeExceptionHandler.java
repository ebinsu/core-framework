package core.framework.web.exception;

import core.framework.exception.BaseRuntimeException;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author ebin
 */
public class BaseRuntimeExceptionHandler implements ExceptionHandler {
    @Override
    public ExceptionResponse handleHeaderAndMessage(HttpServletResponse response, Exception ex) {
        BaseRuntimeException exception = (BaseRuntimeException) ex;
        response.setHeader("error_code", exception.errorCode());
        return responseMessage(ex.getMessage(), exception.errorCode());
    }

    @Override
    public boolean support(Exception ex) {
        return ex instanceof BaseRuntimeException;
    }
}
