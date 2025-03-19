package core.framework.namedquery.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * @author ebin
 */
public class TestHibernateNamedQueryDatasourceProvider implements HibernateNamedQueryDatasourceProvider {
    private EntityManager entityManager;

    @Override
    public EntityManager get() {
        return entityManager;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
