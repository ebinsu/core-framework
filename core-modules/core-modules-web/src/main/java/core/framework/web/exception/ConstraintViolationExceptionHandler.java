package core.framework.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import java.util.stream.Collectors;

import static core.framework.web.exception.DefaultHandlerExceptionResolver.ERROR_CODE_ATTRIBUTE;

/**
 * @author ebin
 */
public class ConstraintViolationExceptionHandler implements ExceptionHandler {
    @Override
    public ExceptionResponse getResponseMessage(HttpServletRequest request, Exception ex) {
        if (ex instanceof ConstraintViolationException exception) {
            String errorMsg = exception.getConstraintViolations().stream()
                    .map(constraintViolation ->
                            constraintViolation.getPropertyPath().toString() + constraintViolation.getMessage())
                    .collect(Collectors.joining(","));
            return responseMessage(errorMsg, (String) request.getAttribute(ERROR_CODE_ATTRIBUTE));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean support(Exception ex) {
        return ex instanceof ConstraintViolationException;
    }
}
