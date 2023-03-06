package core.framework.security.common.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * @author ebin
 */
public class AuthorizationRequestMatcher implements RequestMatcher {
    public AntPathMatcher antPathMatcher = new AntPathMatcher();
    public final List<String> patterns;

    public AuthorizationRequestMatcher(List<String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return patterns.stream().anyMatch(p -> antPathMatcher.match(p, request.getRequestURI()));
    }
}
