package core.framework.jpa.common.configuration;

import core.framework.jpa.common.support.DomainEventStoreInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class JPACommonConfiguration {
    @Bean
    public DomainEventStoreInitializer domainEventStoreInitializer() {
        return new DomainEventStoreInitializer();
    }
}
