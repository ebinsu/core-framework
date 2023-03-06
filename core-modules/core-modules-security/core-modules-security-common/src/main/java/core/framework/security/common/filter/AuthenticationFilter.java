package core.framework.security.common.filter;

import core.framework.json.JSON;
import core.framework.web.exception.ExceptionResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author ebin
 */
public class AuthenticationFilter extends OncePerRequestFilter {
    public static final String ERROR_CODE = "AUTHENTICATE_FAILED";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            boolean authenticate = request.authenticate(response);
            if (authenticate) {
                response(response, HttpStatus.OK, Map.of("session_id", request.getSession().getId()));
            }
        } catch (ServletException exception) {
            response(response, HttpStatus.BAD_REQUEST, new ExceptionResponse(ERROR_CODE, exception.getMessage()));
        }
    }

    private void response(HttpServletResponse response, HttpStatus status, Object body) throws IOException {
        response.setStatus(status.value());
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(JSON.toJSON(body));
        out.flush();
    }
}
