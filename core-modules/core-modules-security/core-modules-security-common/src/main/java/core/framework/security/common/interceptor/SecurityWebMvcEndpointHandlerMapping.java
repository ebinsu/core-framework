package core.framework.security.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author ebin
 */
public class SecurityWebMvcEndpointHandlerMapping extends RequestMappingInfoHandlerMapping implements InitializingBean {
    private final Method handleMethod = ReflectionUtils.findMethod(ServletWebOperationHandler.class, "handle", HttpServletRequest.class, HttpServletResponse.class);
    private final RequestMappingInfo.BuilderConfiguration builderConfiguration;
    private List<ServletWebOperation> operations;

    public SecurityWebMvcEndpointHandlerMapping(List<ServletWebOperation> operations) {
        builderConfiguration = new RequestMappingInfo.BuilderConfiguration();
        builderConfiguration.setPatternParser(new PathPatternParser());
        this.operations = List.copyOf(operations);
        setOrder(-100);
    }

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return false;
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        return null;
    }

    @Override
    protected void initHandlerMethods() {
        operations.forEach(operation -> {
            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(operation.path())
                    .options(builderConfiguration)
                    .methods(operation.method())
                    .build();
            registerMapping(
                    requestMappingInfo,
                    new ServletWebOperationHandler(operation),
                    this.handleMethod
            );
        });

    }

    public interface ServletWebOperation {
        Object handle(HttpServletRequest request, HttpServletResponse response);

        String path();

        RequestMethod method();
    }

    private record ServletWebOperationHandler(ServletWebOperation operation) {
        @Reflective
        @ResponseBody
        Object handle(HttpServletRequest request, HttpServletResponse response) {
            return this.operation.handle(request, response);
        }

        @Override
        public String toString() {
            return this.operation.toString();
        }

    }
}
