package core.framework.test;

import core.framework.test.web.EmailCodeIdentityManager;
import io.undertow.security.idm.IdentityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class Config {

    @Bean
    public IdentityManager identityManager() {
        return new EmailCodeIdentityManager();
    }
}
