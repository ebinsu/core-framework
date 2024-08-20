package core.framework.web.common.exception;

import java.util.List;

/**
 * @author ebin
 */
@FunctionalInterface
public interface ExceptionHandlerCustomizer {
    List<ExceptionHandler> exceptionHandlers();
}
