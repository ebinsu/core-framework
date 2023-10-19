package core.framework.jpa.hibernate.mysql.configuration;

import com.mysql.cj.conf.PropertyKey;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import core.framework.jpa.common.support.ConfigurableEntityManagerFactoryBean;
import core.framework.jpa.common.support.ConfigurablePersistenceUnitInfo;
import core.framework.jpa.common.support.DomainEventPersistenceDriver;
import core.framework.jpa.hibernate.mysql.DomainEventTracking;
import core.framework.jpa.hibernate.mysql.support.DDDPersistenceManagedTypesScanner;
import core.framework.jpa.hibernate.mysql.support.MysqlDomainEventPersistenceDriver;
import core.framework.jpa.hibernate.mysql.support.MysqlPersistenceUnitCustomizer;
import core.framework.jpa.hibernate.mysql.support.SpringHibernateJpaPersistenceProvider;
import core.framework.mysql.MySQLQueryInterceptor;
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
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
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
@EnableConfigurationProperties({HibernateMysqlProperties.class})
public class HibernateConfiguration {
    public static final int STATEMENT_FETCH_SIZE = 64;
    public static final String MYSQL_PERSISTENCE_UNIT_INFO_NAME = "mysql";
    public static final String MYSQL_PERSISTENCE_UNIT_INFO_BEAN_NAME = "mysqlPersistenceUnitInfo";
    public static final String MYSQL_ENTITY_MANAGER_FACTORY_BEAN_NAME = "mysqlEntityManagerFactory";
    public static final String MYSQL_TRANSACTION_MANAGER_BEAN_NAME = "mysqlTransactionManager";

    private final HibernateMysqlProperties jpaMysqlProperties;

    public HibernateConfiguration(HibernateMysqlProperties jpaMysqlProperties) {
        this.jpaMysqlProperties = jpaMysqlProperties;
    }

    @Bean
    public HikariDataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());
        hikariConfig.setJdbcUrl(jpaMysqlProperties.getJdbcUrl());
        hikariConfig.setUsername(jpaMysqlProperties.getUsername());
        hikariConfig.setPassword(jpaMysqlProperties.getPassword());
        hikariConfig.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        hikariConfig.setAutoCommit(false);
        hikariConfig.addDataSourceProperty(PropertyKey.queryInterceptors.getKeyName(), MySQLQueryInterceptor.class.getName());
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = MYSQL_PERSISTENCE_UNIT_INFO_BEAN_NAME)
    public ConfigurablePersistenceUnitInfo mysqlPersistenceUnitInfo(DataSource dataSource, ObjectProvider<MysqlPersistenceUnitCustomizer> customizers) {
        Properties properties = new Properties();
        properties.put(AvailableSettings.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, "true");
        properties.put(AvailableSettings.SHOW_SQL, "true");
        properties.putIfAbsent(AvailableSettings.JAKARTA_VALIDATION_MODE, ValidationMode.AUTO);
        properties.putIfAbsent(AvailableSettings.ISOLATION, Connection.TRANSACTION_READ_COMMITTED);
        properties.putIfAbsent(AvailableSettings.STATEMENT_FETCH_SIZE, STATEMENT_FETCH_SIZE);
        properties.putIfAbsent(AvailableSettings.JAKARTA_SHARED_CACHE_MODE, SharedCacheMode.UNSPECIFIED);
        properties.putIfAbsent(AvailableSettings.PHYSICAL_NAMING_STRATEGY, CamelCaseToUnderscoresNamingStrategy.class.getName());

        ConfigurablePersistenceUnitInfo configurablePersistenceUnitInfo = new ConfigurablePersistenceUnitInfo(MYSQL_PERSISTENCE_UNIT_INFO_NAME);
        configurablePersistenceUnitInfo.setPackagesToScan(jpaMysqlProperties.getPackagesToScan());
        configurablePersistenceUnitInfo.setPersistenceProviderClassName(SpringHibernateJpaPersistenceProvider.class.getName());
        configurablePersistenceUnitInfo.addManagedClassName(DomainEventTracking.class.getName());
        configurablePersistenceUnitInfo.setProperties(properties);
        configurablePersistenceUnitInfo.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
        configurablePersistenceUnitInfo.setNonJtaDataSource(dataSource);

        customizers.orderedStream().forEach(customizer -> customizer.customize(configurablePersistenceUnitInfo));
        return configurablePersistenceUnitInfo;
    }

    @Bean(name = MYSQL_ENTITY_MANAGER_FACTORY_BEAN_NAME)
    public ConfigurableEntityManagerFactoryBean mysqlEntityManagerFactory(@Autowired @Qualifier(MYSQL_PERSISTENCE_UNIT_INFO_BEAN_NAME) ConfigurablePersistenceUnitInfo mysqlPersistenceUnitInfo) {
        return new ConfigurableEntityManagerFactoryBean(mysqlPersistenceUnitInfo);
    }

    @Bean(name = MYSQL_TRANSACTION_MANAGER_BEAN_NAME)
    public PlatformTransactionManager transactionManager(@Autowired @Qualifier(MYSQL_ENTITY_MANAGER_FACTORY_BEAN_NAME) EntityManagerFactory mysqlEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(mysqlEntityManager);
        transactionManager.setDefaultTimeout(30);
        transactionManager.setRollbackOnCommitFailure(true);
        return transactionManager;
    }

    @Bean
    public DomainEventPersistenceDriver mysqlDomainEventPersistenceDriver() {
        return new MysqlDomainEventPersistenceDriver();
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
