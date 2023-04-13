package core.framework.security.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
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
