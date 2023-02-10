package core.framework.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;

import java.io.IOException;
import java.util.List;

/**
 * @author ebin
 */
public class VirtualFilterChain implements FilterChain {
    private static final Log logger = LogFactory.getLog(FilterChainProxy.class);

    private final FilterChain originalChain;

    private final List<Filter> additionalFilters;

    private final int size;

    private int currentPosition = 0;

    public VirtualFilterChain(FilterChain chain, List<Filter> additionalFilters) {
        this.originalChain = chain;
        this.additionalFilters = additionalFilters;
        this.size = additionalFilters.size();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (this.currentPosition == this.size) {
            this.originalChain.doFilter(request, response);
            return;
        }
        this.currentPosition++;
        Filter nextFilter = this.additionalFilters.get(this.currentPosition - 1);
        if (logger.isTraceEnabled()) {
            String name = nextFilter.getClass().getSimpleName();
            logger.trace(LogMessage.format("Invoking %s (%d/%d)", name, this.currentPosition, this.size));
        }
        nextFilter.doFilter(request, response, this);
    }

}
