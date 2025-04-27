package core.framework.web.common.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author ebin
 */
public interface RouteDefinition {
    Object handle(HttpServletRequest request, HttpServletResponse response);

    String path();

    RequestMethod method();
}
