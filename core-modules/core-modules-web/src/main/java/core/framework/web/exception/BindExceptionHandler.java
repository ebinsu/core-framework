package core.framework.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static core.framework.web.exception.DefaultHandlerExceptionResolver.ERROR_CODE_ATTRIBUTE;

/**
 * @author ebin
 */
public class BindExceptionHandler implements ExceptionHandler {
    @Override
    public ExceptionResponse getResponseMessage(HttpServletRequest request, Exception ex) {
        StringBuilder errorMsg = new StringBuilder();
        if (ex instanceof BindException exception) {
            Map<String, List<FieldError>> errorMap = exception.getFieldErrors().stream().collect(Collectors.groupingBy(k -> k.getField()));
            errorMap.forEach((filed, errors) ->
                    errorMsg.append(filed)
                            .append(
                                    errors.stream()
                                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                            .collect(Collectors.joining(","))
                            )
                            .append(';'));
            return responseMessage(errorMsg.toString(), (String) request.getAttribute(ERROR_CODE_ATTRIBUTE));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean support(Exception ex) {
        return ex instanceof BindException;
    }
}
