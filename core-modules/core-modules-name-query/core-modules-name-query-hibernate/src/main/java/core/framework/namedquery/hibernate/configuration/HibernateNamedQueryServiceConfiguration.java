package core.framework.namedquery.hibernate.configuration;


import core.framework.namedquery.NamedQueryExecutor;
import core.framework.namedquery.NamedQueryExecutorProvider;
import core.framework.namedquery.QueryType;
import core.framework.namedquery.hibernate.HibernateNamedQueryDatasourceProvider;
import core.framework.namedquery.hibernate.HibernateNamedQueryExecutor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class HibernateNamedQueryServiceConfiguration {
    public static final String HIBERNATE_MYSQL_QUERY_EXECUTOR = "hibernateNamedQueryExecutor";

    @Bean(name = HIBERNATE_MYSQL_QUERY_EXECUTOR)
    public NamedQueryExecutor hibernateNameQueryExecutor(@Autowired HibernateNamedQueryDatasourceProvider hibernateNamedQueryDatasourceProvider) {
        return new HibernateNamedQueryExecutor(hibernateNamedQueryDatasourceProvider.get());
    }

    @Bean
    public NamedQueryExecutorProvider hibernateNameQueryExecutorProvider(@Autowired @Qualifier(HIBERNATE_MYSQL_QUERY_EXECUTOR) NamedQueryExecutor hibernateNameQueryExecutor) {
        return () -> Pair.of(QueryType.SQL, hibernateNameQueryExecutor);
    }
}
