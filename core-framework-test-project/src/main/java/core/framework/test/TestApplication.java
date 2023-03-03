package core.framework.test;

import core.framework.security.common.configuration.SecurityConfiguration;
import core.framework.security.undertow.configuration.UndertowConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author ebin
 */
@SpringBootApplication
@Import({SecurityConfiguration.class, UndertowConfig.class})
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
