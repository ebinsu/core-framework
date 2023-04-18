package core.framework.security.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author ebin
 */
public record LogoutOperation(String path,
                              RequestMethod method) implements SecurityWebMvcEndpointHandlerMapping.ServletWebOperation {

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }
        return Map.of();
    }

}
