package core.framework.namedquery.jdbc;

import javax.sql.DataSource;

/**
 * @author ebin
 */
@FunctionalInterface
public interface JdbcNamedQueryDatasourceProvider {
    DataSource get();
}
