package core.framework.jpa.hibernate.support;

import javax.sql.DataSource;

/**
 * @author ebin
 */
@FunctionalInterface
public interface ConfigurablePersistenceUnitDataSourceProvider {
    DataSource get();
}
