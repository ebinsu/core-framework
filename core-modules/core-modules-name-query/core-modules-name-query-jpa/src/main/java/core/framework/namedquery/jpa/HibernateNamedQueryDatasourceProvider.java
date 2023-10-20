package core.framework.namedquery.jpa;

import jakarta.persistence.EntityManager;

/**
 * @author ebin
 */
@FunctionalInterface
public interface HibernateNamedQueryDatasourceProvider {
    EntityManager get();
}
