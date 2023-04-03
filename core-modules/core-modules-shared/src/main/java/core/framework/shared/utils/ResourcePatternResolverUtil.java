package core.framework.shared.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public final class ResourcePatternResolverUtil {
    public static final String CLASSPATH_PREFIX = "classpath:";

    private ResourcePatternResolverUtil() {
    }

    public static List<Resource> resolve(String locationPattern, Predicate<Resource>... predicate) throws IOException {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourceResolver.getResources(CLASSPATH_PREFIX + locationPattern);
        Predicate<Resource> resourcePredicate;
        if (predicate != null && predicate.length > 1) {
            resourcePredicate = predicate[0];
            return Arrays.stream(resources).filter(resourcePredicate).collect(Collectors.toList());
        }
        return Arrays.stream(resources).collect(Collectors.toList());

    }
}
