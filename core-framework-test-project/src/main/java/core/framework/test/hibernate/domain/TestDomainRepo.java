package core.framework.test.hibernate.domain;

import core.framework.ddd.Repository;
import core.framework.jpa.hibernate.AbstractJPARepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * @author ebin
 */
@org.springframework.stereotype.Repository
public class TestDomainRepo extends AbstractJPARepository<TestDomain> implements Repository<TestDomain> {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
