package core.framework.test.hibernate.domain;

import core.framework.ddd.Repository;
import core.framework.jpa.hibernate.AbstractJPARepository;
import core.framework.test.hibernate.domain.TestDomain;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * @author ebin
 */
@org.springframework.stereotype.Repository
public class TestDomainRepo extends AbstractJPARepository<TestDomain, Long> implements Repository<TestDomain, Long> {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
