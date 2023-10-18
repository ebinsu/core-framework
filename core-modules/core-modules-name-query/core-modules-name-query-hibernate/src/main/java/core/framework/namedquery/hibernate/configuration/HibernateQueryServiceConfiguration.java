package core.framework.namedquery.hibernate.configuration;


import core.framework.namedquery.NamedQueryExecutor;
import core.framework.namedquery.NamedQueryExecutorProvider;
import core.framework.namedquery.QueryType;
import core.framework.namedquery.hibernate.HibernateNamedQueryExecutor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class HibernateQueryServiceConfiguration {
    public static final String HIBERNATE_MYSQL_QUERY_EXECUTOR = "hibernateNamedQueryExecutor";

    //TODO
    @PersistenceContext
    private EntityManager entityManager;

    @Bean(name = HIBERNATE_MYSQL_QUERY_EXECUTOR)
    public NamedQueryExecutor hibernateNameQueryExecutor() {
        return new HibernateNamedQueryExecutor(entityManager);
    }

    @Bean
    public NamedQueryExecutorProvider hibernateNameQueryExecutorProvider(@Autowired @Qualifier(HIBERNATE_MYSQL_QUERY_EXECUTOR) NamedQueryExecutor hibernateNameQueryExecutor) {
        return () -> Pair.of(QueryType.SQL, hibernateNameQueryExecutor);
    }
}
