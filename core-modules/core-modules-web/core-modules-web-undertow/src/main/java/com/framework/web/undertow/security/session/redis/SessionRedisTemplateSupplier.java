package com.framework.web.undertow.security.session.redis;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author ebin
 */
public interface SessionRedisTemplateSupplier {
    StringRedisTemplate get();
}
