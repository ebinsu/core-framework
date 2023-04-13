package core.framework.web.exception;

import core.framework.exception.BaseRuntimeException;
import core.framework.json.JSON;
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
    public static final String ERROR_CODE_ATTRIBUTE = DefaultHandlerExceptionResolver.class.getName() + ".ERROR.CODE";
    public static final String ERROR_MESSAGE_ATTRIBUTE = DefaultHandlerExceptionResolver.class.getName() + ".ERROR.MESSAGE";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    private final Logger logger = LoggerFactory.getLogger(DefaultHandlerExceptionResolver.class);
    private final MappingJackson2JsonView jsonView;
    private final List<ExceptionHandler> exceptionHandlers = new ArrayList<>();
    private final DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();

    public DefaultHandlerExceptionResolver() {
        this.jsonView = new MappingJackson2JsonView();
        this.jsonView.setExtractValueFromSingleKeyModel(true);
        addDefaultExceptionHandler();
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

        if (ex instanceof BaseRuntimeException e) {
            request.setAttribute(ERROR_CODE_ATTRIBUTE, e.errorCode());
        } else {
            request.setAttribute(ERROR_CODE_ATTRIBUTE, INTERNAL_ERROR);
        }
        request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());

        ExceptionHandler exceptionHandler = exceptionHandlers.stream()
                .filter(f -> f.support(ex)).findFirst()
                .orElse(defaultExceptionHandler);
        ExceptionResponse responseMessage = exceptionHandler.getResponseMessage(request, ex);
        mv.addObject("exception", responseMessage);
        mv.setView(jsonView);
        logger.error("response: " + JSON.toJSON(responseMessage));
        return mv;
    }

    @Override
    protected void logException(Exception ex, HttpServletRequest request) {
        logger.error(ex.getMessage(), ex);
    }

    private void addDefaultExceptionHandler() {
        this.exceptionHandlers.add(new BaseRuntimeExceptionHandler());
    }
}
