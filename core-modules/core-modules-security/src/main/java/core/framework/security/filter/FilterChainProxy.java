package core.framework.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.log.LogMessage;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
public class FilterChainProxy extends OncePerRequestFilter {
    private final VirtualFilterChainDecorator filterChainDecorator = new VirtualFilterChainDecorator();
    private final List<SecurityFilterChain> filterChains = new ArrayList<>();

    public void addSecurityFilterChain(SecurityFilterChain filterChain) {
        filterChains.add(filterChain);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        List<Filter> filters = getFilters(request);
        if (filters == null || filters.size() == 0) {
            this.filterChainDecorator.decorate(chain).doFilter(request, response);
            return;
        }
        filterChainDecorator.decorate(chain, filters).doFilter(request, response);
    }

    private List<Filter> getFilters(HttpServletRequest request) {
        int count = 0;
        for (SecurityFilterChain chain : this.filterChains) {
            if (logger.isTraceEnabled()) {
                logger.trace(LogMessage.format("Trying to match request against %s (%d/%d)", chain, ++count,
                        this.filterChains.size()));
            }
            if (chain.matches(request)) {
                return chain.getFilters();
            }
        }
        return null;
    }
}
