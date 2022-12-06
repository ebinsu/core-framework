package core.framework.query.hibernate.configuration;

import core.framework.query.QueryParser;
import core.framework.query.QueryService;
import core.framework.query.QueryType;
import core.framework.query.configuration.QueryServiceConfiguration;
import core.framework.query.configuration.RouterQueryServiceCustomizer;
import core.framework.query.hibernate.HibernateMysqlQueryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
@AutoConfigureAfter(QueryServiceConfiguration.class)
@ConditionalOnBean(QueryParser.class)
public class HibernateQueryServiceConfiguration {
    public static final String HIBERNATE_MYSQL_QUERY_SERVICE = "hibernateMysqlQueryService";

    @PersistenceContext
    private EntityManager entityManager;

    @Bean(name = HIBERNATE_MYSQL_QUERY_SERVICE)
    public QueryService hibernateMysqlQueryService(@Autowired QueryParser queryParser) {
        return new HibernateMysqlQueryService(entityManager, queryParser);
    }

    @Bean(name = "mysqlRouterQueryServiceCustomizer")
    public RouterQueryServiceCustomizer routerQueryServiceCustomizer(@Autowired @Qualifier(HIBERNATE_MYSQL_QUERY_SERVICE) QueryService hibernateMysqlQueryService) {
        return service -> service.addQueryService(QueryType.SQL, hibernateMysqlQueryService);
    }
}
