package core.framework.ddd.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * @author ebin
 */
public class TestEntityRepo extends AbstractJPARepository<TestEntity> {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
