package core.framework.namedquery.jdbc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author ebin
 */
@Configuration
public class TestConfig {
    @Bean
    public JdbcNamedQueryDatasourceProvider jdbcNamedQueryDatasourceProvider(DataSource dataSource) {
        return () -> dataSource;
    }
}
