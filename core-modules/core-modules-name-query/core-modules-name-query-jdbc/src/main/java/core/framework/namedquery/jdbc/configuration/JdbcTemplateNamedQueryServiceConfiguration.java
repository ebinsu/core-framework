package core.framework.namedquery.jdbc.configuration;

import core.framework.namedquery.NamedQueryExecutorProvider;
import core.framework.namedquery.QueryType;
import core.framework.namedquery.configuration.NamedQueryProperties;
import core.framework.namedquery.jdbc.JdbcTemplateNamedQueryExecutor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author ebin
 */
@Configuration
public class JdbcTemplateNamedQueryServiceConfiguration {

    @Bean
    public JdbcTemplateNamedQueryExecutor jdbcTemplateNamedQueryExecutor(DataSource dataSource,
                                                                         NamedQueryProperties namedQueryProperties) {
        //todo datasource
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return new JdbcTemplateNamedQueryExecutor(jdbcTemplate, namedQueryProperties.getBatchSize());
    }

    @Bean
    public NamedQueryExecutorProvider jdbcTemplateNamedQueryExecutorProvider(JdbcTemplateNamedQueryExecutor jdbcTemplateNamedQueryExecutor) {
        return () -> Pair.of(QueryType.SQL, jdbcTemplateNamedQueryExecutor);
    }
}
