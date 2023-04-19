package core.framework.query.hibernate.configuration;

import core.framework.query.hibernate.HibernateNameQueryExecutor;
import core.framework.query.support.namequery.NameQueryExecutor;
import core.framework.query.support.namequery.NameQueryExecutorProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class HibernateQueryServiceConfiguration {
    public static final String HIBERNATE_MYSQL_QUERY_EXECUTOR = "hibernateNameQueryExecutor";

    @PersistenceContext
    private EntityManager entityManager;

    @Bean(name = HIBERNATE_MYSQL_QUERY_EXECUTOR)
    public NameQueryExecutor hibernateNameQueryExecutor() {
        return new HibernateNameQueryExecutor(entityManager);
    }

    @Bean(name = "mysqlRouterQueryServiceCustomizer")
    public NameQueryExecutorProvider routerQueryServiceCustomizer(@Autowired @Qualifier(HIBERNATE_MYSQL_QUERY_EXECUTOR) NameQueryExecutor hibernateNameQueryExecutor) {
        return () -> hibernateNameQueryExecutor;
    }
}
