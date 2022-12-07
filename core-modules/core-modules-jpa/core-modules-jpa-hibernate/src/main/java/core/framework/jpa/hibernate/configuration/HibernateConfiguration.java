package core.framework.jpa.hibernate.configuration;

import core.framework.jpa.hibernate.support.HibernateDomainEventStoreInitializer;
import jakarta.persistence.ValidationMode;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilderCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;

@Configuration
public class HibernateConfiguration {
    @Bean
    public HibernateDomainEventStoreInitializer hibernateDomainEventStoreInitializer() {
        return new HibernateDomainEventStoreInitializer();
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return prop -> {
            prop.putIfAbsent(AvailableSettings.JAKARTA_VALIDATION_MODE, ValidationMode.AUTO);
            prop.putIfAbsent(AvailableSettings.ISOLATION, Connection.TRANSACTION_READ_COMMITTED);
            prop.putIfAbsent(AvailableSettings.STATEMENT_FETCH_SIZE, 64);
        };
    }

    @Bean
    public EntityManagerFactoryBuilderCustomizer persistenceUnitCustomizer() {
        return builder -> builder.setPersistenceUnitPostProcessors(new PersistenceUnitCustomizer());
    }
}
