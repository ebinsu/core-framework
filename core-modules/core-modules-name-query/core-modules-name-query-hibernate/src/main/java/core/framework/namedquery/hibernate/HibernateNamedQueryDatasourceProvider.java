package core.framework.namedquery.hibernate;

import jakarta.persistence.EntityManager;

/**
 * @author ebin
 */
@FunctionalInterface
public interface HibernateNamedQueryDatasourceProvider {
    EntityManager get();
}
