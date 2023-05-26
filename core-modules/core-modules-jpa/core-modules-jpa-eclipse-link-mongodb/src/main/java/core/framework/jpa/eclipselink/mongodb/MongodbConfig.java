package core.framework.jpa.eclipselink.mongodb;

import core.framework.jpa.common.support.ConfigurableEntityManagerFactoryBean;
import core.framework.jpa.common.support.ConfigurablePersistenceUnitInfo;
import core.framework.jpa.eclipselink.ConfigurablePersistenceUnitInfoPersistenceProvider;
import core.framework.jpa.eclipselink.DomainEventSessionEventListener;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Properties;

/**
 * @author ebin
 */
@Configuration
public class MongodbConfig {
    public static final String MONGODB_PERSISTENCE_UNIT_INFO_NAME = "mongodb";
    public static final String MONGODB_PERSISTENCE_UNIT_INFO_BEAN_NAME = "mongodbPersistenceUnitInfo";
    public static final String MONGODB_ENTITY_MANAGER_FACTORY_BEAN_NAME = "mongodbEntityManagerFactory";
    public static final String MONGODB_TRANSACTION_MANAGER_BEAN_NAME = "mongodbTransactionManager";
    /*private final HibernateMongoDBProperties jpaMongodbProperties;

    public MongodbConfig(HibernateMongoDBProperties jpaMongodbProperties) {
        this.jpaMongodbProperties = jpaMongodbProperties;
    }*/

    @Bean
    public EntityManagerHolder entityManagerHolder() {
        return new EntityManagerHolder();
    }

    @Bean(name = MONGODB_PERSISTENCE_UNIT_INFO_BEAN_NAME)
    public ConfigurablePersistenceUnitInfo mongodbPersistenceUnitInfo() {
        Properties properties = new Properties();
        properties.put("eclipselink.nosql.property.mongo.port", "30002");
        properties.put("eclipselink.nosql.property.mongo.host", "192.168.136.128");
        properties.put("eclipselink.nosql.property.mongo.db", "demo");
        properties.put("eclipselink.logging.level", "OFF");
        properties.put("eclipselink.target-database", org.eclipse.persistence.nosql.adapters.mongo.MongoPlatform.class.getName());
        properties.put("eclipselink.nosql.connection-spec", org.eclipse.persistence.nosql.adapters.mongo.MongoConnectionSpec.class.getName());
        properties.put("eclipselink.session-event-listener", DomainEventSessionEventListener.class.getName());

//        if (StringUtils.hasText(jpaMongodbProperties.getAuthenticationDatabase())) {
//            properties.put(MongoDBProperties.USERNAME, jpaMongodbProperties.getUsername());
//            properties.put(MongoDBProperties.PASSWORD, jpaMongodbProperties.getPassword());
//            properties.put(MongoDBProperties.AUTHENTICATION_DATABASE, jpaMongodbProperties.getAuthenticationDatabase());
//        }

//        properties.put(MongoDBProperties.READ_PREFERENCE, ReadPreferenceType.SECONDARY_PREFERRED);
//        properties.put(MongoDBProperties.GRID_DIALECT, HibernateMongoDBDialect.class.getName());
//        properties.put(AvailableSettings.TC_CLASSLOADER, TcclLookupPrecedence.BEFORE.toString());
//        properties.putIfAbsent(AvailableSettings.JPA_VALIDATION_MODE, ValidationMode.AUTO);

        ConfigurablePersistenceUnitInfo configurablePersistenceUnitInfo = new ConfigurablePersistenceUnitInfo(MONGODB_PERSISTENCE_UNIT_INFO_NAME);
        configurablePersistenceUnitInfo.setPersistenceProviderClassName(ConfigurablePersistenceUnitInfoPersistenceProvider.class.getName());
        configurablePersistenceUnitInfo.setPackagesToScan(List.of("core.*"));
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
