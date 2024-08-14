package core.framework.web.exception;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author ebin
 */
public class DefaultExceptionHandler implements ExceptionHandler {
    public static final String ERROR_CODE = "INTERNAL_ERROR";

    @Override
    public ExceptionResponse getResponseMessage(HttpServletRequest request, Exception ex) {
        return responseMessage(ex.getMessage(), ERROR_CODE);
    }

    @Override
    public boolean support(Exception ex) {
        return true;
    }
}
