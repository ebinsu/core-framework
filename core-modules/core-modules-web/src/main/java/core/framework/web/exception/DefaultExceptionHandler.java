package core.framework.web.exception;

import jakarta.servlet.http.HttpServletRequest;

import static core.framework.web.exception.DefaultHandlerExceptionResolver.ERROR_CODE_ATTRIBUTE;

/**
 * @author ebin
 */
public class DefaultExceptionHandler implements ExceptionHandler {
    @Override
    public ExceptionResponse getResponseMessage(HttpServletRequest request, Exception ex) {
        return responseMessage(ex.getMessage(), (String) request.getAttribute(ERROR_CODE_ATTRIBUTE));
    }

    @Override
    public boolean support(Exception ex) {
        return true;
    }
}
