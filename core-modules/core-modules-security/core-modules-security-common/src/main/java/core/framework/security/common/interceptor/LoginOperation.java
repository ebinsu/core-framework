package core.framework.security.common.interceptor;

import core.framework.security.common.exception.AuthenticateFailedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.Map;

/**
 * @author ebin
 */
public class LoginOperation implements SecurityWebMvcEndpointHandlerMapping.ServletWebOperation {
    private String path = "/login";
    private RequestMethod method = RequestMethod.PUT;

    public LoginOperation() {
    }

    public LoginOperation(String path, RequestMethod method) {
        this.path = path;
        this.method = method;
    }

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response) {
        try {
            boolean authenticate = request.authenticate(response);
            if (!authenticate) {
                throw new AuthenticateFailedException();
            }
        } catch (IOException | ServletException e) {
            throw new AuthenticateFailedException();
        }
        return Map.of("session", request.getSession().getId());
    }

    public String getPath() {
        return path;
    }

    public RequestMethod getMethod() {
        return method;
    }

}
