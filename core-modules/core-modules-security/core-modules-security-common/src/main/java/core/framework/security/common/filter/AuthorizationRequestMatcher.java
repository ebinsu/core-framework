package core.framework.security.common.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * @author ebin
 */
public class AuthorizationRequestMatcher implements RequestMatcher {
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final List<String> patterns;
    private final List<String> excludePatterns;

    public AuthorizationRequestMatcher(List<String> patterns, List<String> excludePatterns) {
        this.patterns = List.copyOf(patterns);
        this.excludePatterns = List.copyOf(excludePatterns);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (excludePatterns.stream().anyMatch(p -> antPathMatcher.match(p, request.getRequestURI()))) {
            return false;
        } else {
            return patterns.stream().anyMatch(p -> antPathMatcher.match(p, request.getRequestURI()));
        }
    }
}
