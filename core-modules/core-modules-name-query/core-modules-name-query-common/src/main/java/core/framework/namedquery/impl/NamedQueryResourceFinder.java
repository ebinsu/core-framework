package core.framework.namedquery.impl;

import core.framework.kernel.utils.ResourcePatternResolverUtil;
import core.framework.namedquery.support.NamedQueryResourceHolder;
import core.framework.namedquery.support.ResolverContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * @author ebin
 */
public class NamedQueryResourceFinder implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(NamedQueryResourceFinder.class);
    private final ResolverContext resolverContext;
    private List<NamedQueryResourceHolder> namedQueryResourceHolders;

    public NamedQueryResourceFinder(ResolverContext resolverContext) {
        this.resolverContext = resolverContext;
    }

    public List<NamedQueryResourceHolder> getNamedQueryResourceHolders() {
        return Collections.unmodifiableList(namedQueryResourceHolders);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Resource> resources;
        try {
            resources = ResourcePatternResolverUtil.resolve("**/*Query.xml");
        } catch (IOException e) {
            throw new Error(e);
        }
        this.namedQueryResourceHolders = resources.stream().map(resource -> new NamedQueryResourceHolder(this.resolverContext, resource)).toList();
        stopWatch.stop();
        long elapsed = stopWatch.getTotalTimeNanos();
        LOGGER.info("Finished find {} named query resource in {} ms.", resources.size(), Duration.ofNanos(elapsed));
    }
}
