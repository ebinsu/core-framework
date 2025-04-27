package core.framework.web.common.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;

/**
 * @author ebin
 */
public class RouterHandler {
    public static final Method HANDLE_METHOD = ReflectionUtils.findMethod(RouterHandler.class, "handle", HttpServletRequest.class, HttpServletResponse.class);

    private final RouteDefinition routeDefinition;

    public RouterHandler(RouteDefinition routeDefinition) {
        this.routeDefinition = routeDefinition;
    }

    @Reflective
    @ResponseBody
    Object handle(HttpServletRequest request, HttpServletResponse response) {
        return this.routeDefinition.handle(request, response);
    }
}
