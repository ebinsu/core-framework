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

/**
 * @author ebin
 */
public class AJAXAuthenticationFilter extends OncePerRequestFilter {
    public static final String ERROR_CODE = "AUTHENTICATE_FAILED";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            request.authenticate(response);
        } catch (ServletException exception) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(JSON.toJSON(new ExceptionResponse(ERROR_CODE, exception.getMessage())));
            out.flush();
        }
    }
}
