package core.test;

import core.framework.jpa.common.AbstractJPARepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/**
 * @author ebin
 */
@Repository
public class TestDomainRepo extends AbstractJPARepository<TestDomain, String> {
    @PersistenceContext(unitName = "mongodb")
    EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
