package core.framework.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;

import java.util.List;

/**
 * @author ebin
 */
public class VirtualFilterChainDecorator {
    public FilterChain decorate(FilterChain original) {
        return original;
    }

    public FilterChain decorate(FilterChain original, List<Filter> filters) {
        return new VirtualFilterChain(original, filters);
    }
}
