package core.framework.test;

import core.framework.namedquery.jpa.HibernateNamedQueryDatasourceProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * @author ebin
 */
public class HibernateNamedQueryDatasourceProviderImpl implements HibernateNamedQueryDatasourceProvider {
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public EntityManager get() {
        return entityManager;
    }
}
