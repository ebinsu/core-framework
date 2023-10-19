package core.framework.namedquery.hibernate;

import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilderCustomizer;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class TestConfig {
    @Bean
    public EntityManagerFactoryBuilderCustomizer entityManagerFactoryBuilderCustomizer() {
        return new EntityManagerFactoryBuilderCustomizer() {
            @Override
            public void customize(EntityManagerFactoryBuilder builder) {
                builder.setPersistenceUnitPostProcessors(pui -> pui.addManagedClassName(TestEntity.class.getName()));
            }
        };
    }

    @Bean
    public HibernateNamedQueryDatasourceProvider hibernateNamedQueryDatasourceProvider() {
        return new TestHibernateNamedQueryDatasourceProvider();
    }
}
