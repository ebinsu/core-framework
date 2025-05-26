package core.framework.web.common.exception;

import core.framework.kernel.log.marker.ErrorCodeMarker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
public class DefaultHandlerExceptionResolver extends AbstractHandlerExceptionResolver {
    private final Logger logger = LoggerFactory.getLogger(DefaultHandlerExceptionResolver.class);
    private final MappingJackson2JsonView jsonView;
    private final List<ExceptionHandler> exceptionHandlers = new ArrayList<>();
    private final DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();

    public DefaultHandlerExceptionResolver() {
        this.jsonView = new MappingJackson2JsonView();
        this.jsonView.setExtractValueFromSingleKeyModel(true);
    }

    public void addExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandlers.add(exceptionHandler);
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView mv = new ModelAndView();

        ResponseStatus responseStatus = ex.getClass().getDeclaredAnnotation(ResponseStatus.class);
        if (responseStatus == null) {
            mv.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            mv.setStatus(responseStatus.value());
        }

        ExceptionHandler exceptionHandler = exceptionHandlers.stream()
            .filter(f -> f.support(ex)).findFirst()
            .orElse(defaultExceptionHandler);
        ExceptionResponse responseMessage = exceptionHandler.getResponseMessage(request, ex);
        mv.addObject("responseBody", responseMessage);
        mv.setView(jsonView);
        logger.error(new ErrorCodeMarker(responseMessage.errorCode()), responseMessage.message(), ex);
        return mv;
    }
}
