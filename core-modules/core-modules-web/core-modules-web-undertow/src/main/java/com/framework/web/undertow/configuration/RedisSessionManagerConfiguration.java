package com.framework.web.undertow.configuration;

import com.framework.web.undertow.security.session.redis.SessionRedisTemplateSupplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
@ConditionalOnBean(SessionRedisTemplateSupplier.class)
public class RedisSessionManagerConfiguration {

    @Bean
    public UndertowDeploymentInfoCustomizer undertowRedisSessionManagerCustomizer(SessionRedisTemplateSupplier sessionRedisTemplateSupplier) {
        return new RedisSessionManagerCustomizer(sessionRedisTemplateSupplier.get());
    }
}
