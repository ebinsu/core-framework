package core.framework.security.common.interceptor;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author ebin
 */
public class LogoutOperation implements SecurityWebMvcEndpointHandlerMapping.ServletWebOperation {
    private String path = "/logout";
    private RequestMethod method = RequestMethod.PUT;
    private final MediaType type = MediaType.APPLICATION_JSON;

    public LogoutOperation() {
    }

    public LogoutOperation(String path, RequestMethod method) {
        this.path = path;
        this.method = method;
    }

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.logout();
        } catch (ServletException ignored) {

        }
        return Map.of();
    }

    public String getPath() {
        return path;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public MediaType getType() {
        return type;
    }

}
