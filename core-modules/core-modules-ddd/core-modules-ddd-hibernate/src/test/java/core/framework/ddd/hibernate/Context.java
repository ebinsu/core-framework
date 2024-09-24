package core.framework.ddd.hibernate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class Context {
    @Bean
    public TestEntityRepo testEntityRepo() {
        return new TestEntityRepo();
    }

    @Bean
    public CreatedEventHandler createdEventHandler() {
        return new CreatedEventHandler();
    }
}
