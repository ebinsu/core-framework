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
public record LoginOperation(String path,
                             RequestMethod method) implements SecurityWebMvcEndpointHandlerMapping.ServletWebOperation {

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

}
