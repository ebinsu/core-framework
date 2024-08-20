package com.framework.web.undertow.configuration;

import com.framework.web.undertow.security.session.redis.SessionRedisTemplateSupplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author ebin
 */
@ConditionalOnClass(RedisTemplate.class)
@Configuration
public class RedisSessionManagerConfiguration {
    @Bean
    @ConditionalOnBean(SessionRedisTemplateSupplier.class)
    public UndertowDeploymentInfoCustomizer undertowRedisSessionManagerCustomizer(SessionRedisTemplateSupplier sessionRedisTemplateSupplier) {
        return new RedisSessionManagerCustomizer(sessionRedisTemplateSupplier.get());
    }
}
