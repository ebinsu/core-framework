package core.framework.security.common.filter;

import core.framework.json.JSON;
import core.framework.security.common.SecurityContext;
import core.framework.web.exception.ExceptionResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.Set;

/**
 * @author ebin
 */
public class AuthorizationFilter extends OncePerRequestFilter {
    private final AuthorizationPermissionSupplier permissionSupplier;

    public AuthorizationFilter(AuthorizationPermissionSupplier permissionSupplier) {
        this.permissionSupplier = permissionSupplier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!SecurityContext.isAnonymous(request.getMethod(), request.getRequestURI())) {
            HttpSession session = request.getSession(false);
            if (session == null) {
                responseError(response, HttpStatus.UNAUTHORIZED);
                return;
            }
            if (!hasPermissions(request, session)) {
                responseError(response, HttpStatus.FORBIDDEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean hasPermissions(HttpServletRequest request, HttpSession session) {
        Set<String> permissions = Optional.ofNullable(session.getAttribute("permissions")).map(m -> (Set<String>) m).orElse(Set.of());
        return SecurityContext.hasPermission(request.getMethod(), request.getRequestURI(), permissions);
    }

    private void responseError(HttpServletResponse response, HttpStatus httpStatus) throws IOException {
        String errorCode = httpStatus.name();
        String errorMessage = httpStatus.name();
        response.setStatus(httpStatus.value());
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(JSON.toJSON(new ExceptionResponse(errorCode, errorMessage)));
        out.flush();
    }
}
