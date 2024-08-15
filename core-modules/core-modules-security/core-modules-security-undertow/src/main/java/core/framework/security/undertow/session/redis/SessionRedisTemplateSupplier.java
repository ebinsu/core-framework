package core.framework.security.undertow.session.redis;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author ebin
 */
public interface SessionRedisTemplateSupplier {
    StringRedisTemplate get();
}
