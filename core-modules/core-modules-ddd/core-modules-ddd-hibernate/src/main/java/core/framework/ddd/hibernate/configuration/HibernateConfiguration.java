package core.framework.ddd.hibernate.configuration;

import core.framework.ddd.hibernate.DomainEventTracking;
import core.framework.ddd.hibernate.configuration.properties.HibernateProperties;
import core.framework.ddd.hibernate.internal.ConfigurableEntityManagerFactoryBean;
import core.framework.ddd.hibernate.internal.ConfigurablePersistenceUnitInfo;
import core.framework.ddd.hibernate.internal.extend.DDDPersistenceManagedTypesScanner;
import core.framework.ddd.hibernate.internal.extend.SpringHibernateJpaPersistenceProvider;
import core.framework.ddd.hibernate.internal.EntityManagerHolderInitializer;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties({HibernateProperties.class})
public class HibernateConfiguration {
    public static final int STATEMENT_FETCH_SIZE = 64;
    public static final String PERSISTENCE_UNIT_INFO_NAME = "default";
    public static final String PERSISTENCE_UNIT_INFO_BEAN_NAME = "defaultPersistenceUnitInfo";
    public static final String ENTITY_MANAGER_FACTORY_BEAN_NAME = "entityManagerFactory";
    public static final String TRANSACTION_MANAGER_BEAN_NAME = "defaultTransactionManager";

    private final HibernateProperties hibernateProperties;

    public HibernateConfiguration(HibernateProperties hibernateProperties) {
        this.hibernateProperties = hibernateProperties;
    }

    @Bean
    public EntityManagerHolderInitializer domainEventStoreInitializer() {
        return new EntityManagerHolderInitializer();
    }

    @Bean(name = PERSISTENCE_UNIT_INFO_BEAN_NAME)
    public ConfigurablePersistenceUnitInfo persistenceUnitInfo(@Autowired(required = false) DataSource dataSource,
                                                               ObjectProvider<ConfigurablePersistenceUnitCustomizer> customizers) {
        Properties properties = new Properties();
        properties.put(AvailableSettings.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, "true");
        properties.put(AvailableSettings.SHOW_SQL, hibernateProperties.isShowSql());
        properties.putIfAbsent(AvailableSettings.JAKARTA_VALIDATION_MODE, ValidationMode.AUTO);
        properties.putIfAbsent(AvailableSettings.ISOLATION, Connection.TRANSACTION_READ_COMMITTED);
        properties.putIfAbsent(AvailableSettings.STATEMENT_FETCH_SIZE, STATEMENT_FETCH_SIZE);
        properties.putIfAbsent(AvailableSettings.JAKARTA_SHARED_CACHE_MODE, SharedCacheMode.UNSPECIFIED);
        properties.putIfAbsent(AvailableSettings.PHYSICAL_NAMING_STRATEGY, CamelCaseToUnderscoresNamingStrategy.class.getName());
        properties.putIfAbsent(AvailableSettings.HBM2DDL_AUTO, hibernateProperties.getHbm2ddl());

        ConfigurablePersistenceUnitInfo configurablePersistenceUnitInfo = new ConfigurablePersistenceUnitInfo(PERSISTENCE_UNIT_INFO_NAME);
        configurablePersistenceUnitInfo.setPackagesToScan(hibernateProperties.getPackagesToScan());
        configurablePersistenceUnitInfo.setPersistenceProviderClassName(SpringHibernateJpaPersistenceProvider.class.getName());
        configurablePersistenceUnitInfo.addManagedClassName(DomainEventTracking.class.getName());
        configurablePersistenceUnitInfo.setProperties(properties);
        configurablePersistenceUnitInfo.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
        configurablePersistenceUnitInfo.setNonJtaDataSource(dataSource);

        customizers.orderedStream().forEach(customizer -> customizer.customize(configurablePersistenceUnitInfo));
        return configurablePersistenceUnitInfo;
    }

    @Bean(name = ENTITY_MANAGER_FACTORY_BEAN_NAME)
    public ConfigurableEntityManagerFactoryBean entityManagerFactory(@Autowired @Qualifier(PERSISTENCE_UNIT_INFO_BEAN_NAME) ConfigurablePersistenceUnitInfo persistenceUnitInfo) {
        return new ConfigurableEntityManagerFactoryBean(persistenceUnitInfo);
    }

    @Bean(name = TRANSACTION_MANAGER_BEAN_NAME)
    public PlatformTransactionManager transactionManager(@Autowired @Qualifier(ENTITY_MANAGER_FACTORY_BEAN_NAME) EntityManagerFactory entityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager);
        transactionManager.setDefaultTimeout(30);
        transactionManager.setRollbackOnCommitFailure(true);
        return transactionManager;
    }

    @Bean
    public PersistenceManagedTypes persistenceManagedTypes(BeanFactory beanFactory, ResourceLoader resourceLoader) {
        List<String> packages = EntityScanPackages.get(beanFactory).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(beanFactory)) {
            packages = AutoConfigurationPackages.get(beanFactory);
        }
        return new DDDPersistenceManagedTypesScanner(resourceLoader).scan(StringUtils.toStringArray(packages));
    }

    @Bean
    public PersistenceUnitPostProcessor persistenceUnitCustomizer() {
        return new PersistenceUnitCustomizer();
    }
}
