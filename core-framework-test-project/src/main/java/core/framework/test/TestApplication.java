package core.framework.test;

import core.framework.security.configuration.AuthorizationConfiguration;
import core.framework.security.configuration.UndertowCustomizerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author ebin
 */
@SpringBootApplication
@Import({UndertowCustomizerConfig.class, AuthorizationConfiguration.class})
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
