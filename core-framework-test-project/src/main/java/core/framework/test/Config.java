package core.framework.test;

import core.framework.security.undertow.SessionRedisTemplateSupplier;
import core.framework.test.web.AccountFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author ebin
 */
@Configuration
public class Config {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Bean
    public AccountFinder accountFinder() {
        return new AccountFinder();
    }

    @Bean
    public SessionRedisTemplateSupplier sessionRedisTemplateSupplier() {
        return () -> stringRedisTemplate;
    }
}
