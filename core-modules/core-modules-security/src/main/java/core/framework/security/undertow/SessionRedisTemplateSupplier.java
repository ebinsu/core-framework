package core.framework.security.undertow;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author ebin
 */
public interface SessionRedisTemplateSupplier {
    StringRedisTemplate get();
}
