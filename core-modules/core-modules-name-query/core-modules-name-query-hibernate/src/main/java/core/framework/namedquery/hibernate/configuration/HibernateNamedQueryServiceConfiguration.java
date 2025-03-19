package core.framework.namedquery.hibernate.configuration;


import core.framework.namedquery.NamedQueryExecutor;
import core.framework.namedquery.NamedQueryExecutorProvider;
import core.framework.namedquery.configuration.NamedQueryProperties;
import core.framework.namedquery.hibernate.HibernateNamedQueryDatasourceProvider;
import core.framework.namedquery.hibernate.impl.HibernateNamedQueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author ebin
 */
@Configuration
public class HibernateNamedQueryServiceConfiguration {
    public static final String HIBERNATE_MYSQL_QUERY_EXECUTOR = "hibernateNamedQueryExecutor";

    @Bean(name = HIBERNATE_MYSQL_QUERY_EXECUTOR)
    public NamedQueryExecutor hibernateNameQueryExecutor(HibernateNamedQueryDatasourceProvider hibernateNamedQueryDatasourceProvider,
                                                         NamedQueryProperties namedQueryProperties) {
        return new HibernateNamedQueryExecutor(hibernateNamedQueryDatasourceProvider.get(), namedQueryProperties.getBatchSize());
    }

    @Bean
    public NamedQueryExecutorProvider hibernateNameQueryExecutorProvider(@Autowired @Qualifier(HIBERNATE_MYSQL_QUERY_EXECUTOR) NamedQueryExecutor hibernateNameQueryExecutor) {
        return () -> Map.of("sql", hibernateNameQueryExecutor);
    }
}
