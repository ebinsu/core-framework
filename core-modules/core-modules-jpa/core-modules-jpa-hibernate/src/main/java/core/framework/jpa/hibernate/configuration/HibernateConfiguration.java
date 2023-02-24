package core.framework.jpa.hibernate.configuration;

import core.framework.jpa.hibernate.support.DDDPersistenceManagedTypesScanner;
import core.framework.jpa.hibernate.support.HibernateDomainEventStoreInitializer;
import jakarta.persistence.ValidationMode;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilderCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.util.List;

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
            prop.putIfAbsent(AvailableSettings.SHOW_SQL, true);
        };
    }

    @Bean
    public EntityManagerFactoryBuilderCustomizer persistenceUnitCustomizer() {
        return builder -> builder.setPersistenceUnitPostProcessors(new PersistenceUnitCustomizer());
    }

    @Bean
    public PersistenceManagedTypes persistenceManagedTypes(BeanFactory beanFactory, ResourceLoader resourceLoader) {
        List<String> packages = EntityScanPackages.get(beanFactory).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(beanFactory)) {
            packages = AutoConfigurationPackages.get(beanFactory);
        }
        return new DDDPersistenceManagedTypesScanner(resourceLoader).scan(StringUtils.toStringArray(packages));
    }
}
