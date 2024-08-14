package core.framework.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import java.util.stream.Collectors;

/**
 * @author ebin
 */
public class ConstraintViolationExceptionHandler implements ExceptionHandler {
    public static final String ERROR_CODE = "VALIDATION_ERROR";

    @Override
    public ExceptionResponse getResponseMessage(HttpServletRequest request, Exception ex) {
        if (ex instanceof ConstraintViolationException exception) {
            String errorMsg = exception.getConstraintViolations().stream()
                .map(constraintViolation ->
                    constraintViolation.getPropertyPath().toString() + constraintViolation.getMessage())
                .collect(Collectors.joining(","));
            return responseMessage(errorMsg, ERROR_CODE);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean support(Exception ex) {
        return ex instanceof ConstraintViolationException;
    }
}
