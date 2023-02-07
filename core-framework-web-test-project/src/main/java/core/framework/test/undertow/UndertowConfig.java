package core.framework.test.undertow;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
@EnableConfigurationProperties({RedisSessionProperties.class})
public class UndertowConfig {

    @Bean
    public UndertowDeploymentInfoCustomizer undertowRedisSessionManagerCustomizer() {
        return new UndertowRedisSessionManagerCustomizer();
    }
}
