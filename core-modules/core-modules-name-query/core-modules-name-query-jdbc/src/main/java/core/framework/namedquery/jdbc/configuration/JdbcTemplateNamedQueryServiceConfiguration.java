package core.framework.namedquery.jdbc.configuration;

import core.framework.namedquery.NamedQueryExecutorProvider;
import core.framework.namedquery.configuration.NamedQueryProperties;
import core.framework.namedquery.jdbc.JdbcNamedQueryDatasourceProvider;
import core.framework.namedquery.jdbc.impl.JdbcTemplateNamedQueryExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * @author ebin
 */
@Configuration
public class JdbcTemplateNamedQueryServiceConfiguration {

    @Bean
    public JdbcTemplateNamedQueryExecutor jdbcTemplateNamedQueryExecutor(JdbcNamedQueryDatasourceProvider jdbcNamedQueryDatasourceProvider,
                                                                         NamedQueryProperties namedQueryProperties) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(jdbcNamedQueryDatasourceProvider.get());
        return new JdbcTemplateNamedQueryExecutor(jdbcTemplate, namedQueryProperties.getBatchSize());
    }

    @Bean
    public NamedQueryExecutorProvider jdbcTemplateNamedQueryExecutorProvider(JdbcTemplateNamedQueryExecutor jdbcTemplateNamedQueryExecutor) {
        return () -> Map.of("sql", jdbcTemplateNamedQueryExecutor);
    }
}
