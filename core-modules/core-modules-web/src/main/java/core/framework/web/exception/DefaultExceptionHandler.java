package core.framework.web.exception;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author ebin
 */
public class DefaultExceptionHandler implements ExceptionHandler {
    @Override
    public ExceptionResponse handleHeaderAndMessage(HttpServletResponse response, Exception ex) {
        response.setHeader("error_code", "INTERNAL_SERVER_ERROR");
        return responseMessage(ex.getMessage(), "INTERNAL_ERROR");
    }

    @Override
    public boolean support(Exception ex) {
        return true;
    }
}
