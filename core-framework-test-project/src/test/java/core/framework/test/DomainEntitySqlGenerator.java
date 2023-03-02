package core.framework.test;

import core.framework.jpa.hibernate.DomainEventTracking;
import core.framework.jpa.hibernate.configuration.HibernateConfiguration;
import core.framework.jpa.hibernate.support.DDDPersistenceManagedTypesScanner;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.UniqueConstraintSchemaUpdateStrategy;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;

import javax.sql.DataSource;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

/**
 * @author ebin
 */
public class DomainEntitySqlGenerator {
    public static void main(String... strings) {
        List<Class<?>> managerClasses = List.of(DomainEventTracking.class);
        stdoutUpdateSchema(managerClasses);
    }

    public static void stdoutUpdateSchema(List<Class<?>> managerClasses) {
        EntityManagerFactoryBuilderImpl builder = genEntityManagerFactoryBuilder(managerClasses);
        builder.build();
        new SchemaUpdate().setFormat(true).execute(EnumSet.of(TargetType.STDOUT), builder.getMetadata());
    }

    private static EntityManagerFactoryBuilderImpl genEntityManagerFactoryBuilder(List<Class<?>> managerClasses) {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new ClassPathResource("application.yaml"));
        SpringApplication application = new SpringApplication(DataSourceAutoConfiguration.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setBannerMode(Banner.Mode.OFF);
        ApplicationContext applicationContext = application.run();
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        Properties props = new Properties();
        props.put(Environment.DIALECT, MySQLDialect.class);
        props.put(Environment.DATASOURCE, dataSource);
        props.put(Environment.UNIQUE_CONSTRAINT_SCHEMA_UPDATE_STRATEGY, UniqueConstraintSchemaUpdateStrategy.RECREATE_QUIETLY);
        props.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, CamelCaseToUnderscoresNamingStrategy.class.getName());
        PersistenceManagedTypes scan = new DDDPersistenceManagedTypesScanner(applicationContext).scan(DomainEntitySqlGenerator.class.getPackageName());
        DefaultPersistenceUnitManager manager = new DefaultPersistenceUnitManager();
        manager.setPackagesToScan(DomainEntitySqlGenerator.class.getPackageName());
        manager.afterPropertiesSet();
        MutablePersistenceUnitInfo persistenceUnitInfo = (MutablePersistenceUnitInfo) manager.obtainDefaultPersistenceUnitInfo();
        managerClasses.forEach(clazz -> persistenceUnitInfo.addManagedClassName(clazz.getName()));
        scan.getManagedClassNames().forEach(persistenceUnitInfo::addManagedClassName);
        EntityManagerFactoryBuilder builder = Bootstrap.getEntityManagerFactoryBuilder(persistenceUnitInfo, props);
        return (EntityManagerFactoryBuilderImpl) builder;
    }
}
