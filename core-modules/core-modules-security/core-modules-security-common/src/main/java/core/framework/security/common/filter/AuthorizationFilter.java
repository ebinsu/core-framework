package core.framework.security.common.filter;

import core.framework.json.JSON;
import core.framework.security.common.SecurityContextV2;
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
import java.util.Set;

/**
 * @author ebin
 */
public class AuthorizationFilter extends OncePerRequestFilter {
    private final AuthorizationPermissionSupplier permissionSupplier;
    private final SecurityContextV2 securityContext;

    public AuthorizationFilter(AuthorizationPermissionSupplier permissionSupplier, SecurityContextV2 securityContext) {
        this.permissionSupplier = permissionSupplier;
        this.securityContext = securityContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!securityContext.isAnonymous(request)) {
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
        Set<String> permissions = permissionSupplier.getPermissions(session);
        return securityContext.hasPermission(request, permissions);
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
