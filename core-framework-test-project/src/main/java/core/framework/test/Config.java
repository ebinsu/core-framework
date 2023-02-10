package core.framework.test;

import core.framework.test.web.AccountFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class Config {
    @Bean
    public AccountFinder accountFinder() {
        return new AccountFinder();
    }
}
