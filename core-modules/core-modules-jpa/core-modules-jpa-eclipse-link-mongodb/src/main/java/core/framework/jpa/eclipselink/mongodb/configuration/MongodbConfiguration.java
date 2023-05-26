package core.framework.jpa.eclipselink.mongodb.configuration;

import core.framework.jpa.common.support.ConfigurableEntityManagerFactoryBean;
import core.framework.jpa.common.support.ConfigurablePersistenceUnitInfo;
import core.framework.jpa.eclipselink.ConfigurablePersistenceUnitInfoPersistenceProvider;
import core.framework.jpa.eclipselink.DomainEventSessionEventListener;
import core.framework.jpa.eclipselink.mongodb.support.MongodbDomainEventPersistenceDriver;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import org.eclipse.persistence.eis.EISConnectionSpec;
import org.eclipse.persistence.nosql.adapters.mongo.MongoConnectionSpec;
import org.eclipse.persistence.nosql.adapters.mongo.MongoPlatform;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * @author ebin
 */
@Configuration
@EnableConfigurationProperties({MongodbProperties.class})
public class MongodbConfiguration {
    public static final String MONGODB_PERSISTENCE_UNIT_INFO_NAME = "mongodb";
    public static final String MONGODB_PERSISTENCE_UNIT_INFO_BEAN_NAME = "mongodbPersistenceUnitInfo";
    public static final String MONGODB_ENTITY_MANAGER_FACTORY_BEAN_NAME = "mongodbEntityManagerFactory";
    public static final String MONGODB_TRANSACTION_MANAGER_BEAN_NAME = "mongodbTransactionManager";
    private static final String ECLIPSE_LINK_NOSQL_PREFIX = "eclipselink.nosql.property.";
    private final MongodbProperties mongodbProperties;

    public MongodbConfiguration(MongodbProperties mongodbProperties) {
        this.mongodbProperties = mongodbProperties;
    }

    @Bean
    public MongodbDomainEventPersistenceDriver mongodbDomainEventPersistenceDriver() {
        return new MongodbDomainEventPersistenceDriver();
    }

    @Bean(name = MONGODB_PERSISTENCE_UNIT_INFO_BEAN_NAME)
    public ConfigurablePersistenceUnitInfo mongodbPersistenceUnitInfo() {
        Properties properties = new Properties();
        properties.put("eclipselink.logging.level", "OFF");
        properties.put("eclipselink.target-database", MongoPlatform.class.getName());
        properties.put("eclipselink.nosql.connection-spec", MongoConnectionSpec.class.getName());
        properties.put("eclipselink.session-event-listener", DomainEventSessionEventListener.class.getName());

        properties.put(ECLIPSE_LINK_NOSQL_PREFIX + MongoConnectionSpec.PORT, mongodbProperties.getPort());
        properties.put(ECLIPSE_LINK_NOSQL_PREFIX + MongoConnectionSpec.HOST, mongodbProperties.getHost());
        properties.put(ECLIPSE_LINK_NOSQL_PREFIX + MongoConnectionSpec.DB, mongodbProperties.getDb());
        if (StringUtils.hasText(mongodbProperties.getAuthenticationDatabase())) {
            properties.put(ECLIPSE_LINK_NOSQL_PREFIX + EISConnectionSpec.USER, mongodbProperties.getUser());
            properties.put(ECLIPSE_LINK_NOSQL_PREFIX + EISConnectionSpec.PASSWORD, mongodbProperties.getPassword());
            properties.put(ECLIPSE_LINK_NOSQL_PREFIX + MongoConnectionSpec.AUTH_SOURCE, mongodbProperties.getAuthenticationDatabase());
        }

        properties.put(ECLIPSE_LINK_NOSQL_PREFIX + MongoConnectionSpec.READ_PREFERENCE, mongodbProperties.getReadPreference().name());
        properties.put(ECLIPSE_LINK_NOSQL_PREFIX + MongoConnectionSpec.WRITE_CONCERN, mongodbProperties.getWriteConcern().name());
        properties.putIfAbsent(AvailableSettings.JAKARTA_VALIDATION_MODE, ValidationMode.AUTO);

        ConfigurablePersistenceUnitInfo configurablePersistenceUnitInfo = new ConfigurablePersistenceUnitInfo(MONGODB_PERSISTENCE_UNIT_INFO_NAME);
        configurablePersistenceUnitInfo.setPersistenceProviderClassName(ConfigurablePersistenceUnitInfoPersistenceProvider.class.getName());
        configurablePersistenceUnitInfo.setPackagesToScan(mongodbProperties.getPackagesToScan());
        configurablePersistenceUnitInfo.setProperties(properties);
        configurablePersistenceUnitInfo.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);

        return configurablePersistenceUnitInfo;
    }

    @Bean(name = MONGODB_ENTITY_MANAGER_FACTORY_BEAN_NAME)
    public ConfigurableEntityManagerFactoryBean mongodbEntityManagerFactory(@Autowired @Qualifier(MONGODB_PERSISTENCE_UNIT_INFO_BEAN_NAME) ConfigurablePersistenceUnitInfo mongodbPersistenceUnitInfo) {
        return new ConfigurableEntityManagerFactoryBean(mongodbPersistenceUnitInfo);
    }

    @Bean(name = MONGODB_TRANSACTION_MANAGER_BEAN_NAME)
    public PlatformTransactionManager transactionManager(@Autowired @Qualifier(MONGODB_ENTITY_MANAGER_FACTORY_BEAN_NAME) EntityManagerFactory mongodbEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(mongodbEntityManager);
        return transactionManager;
    }

}
