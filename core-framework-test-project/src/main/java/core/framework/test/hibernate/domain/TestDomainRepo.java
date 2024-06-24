package core.framework.test.hibernate.domain;

import core.framework.ddd.Repository;
import core.framework.jpa.common.AbstractJPARepository;
import core.framework.jpa.hibernate.configuration.HibernateConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * @author ebin
 */
@org.springframework.stereotype.Repository
public class TestDomainRepo extends AbstractJPARepository<TestDomain> implements Repository<TestDomain> {
    @PersistenceContext(unitName = HibernateConfiguration.PERSISTENCE_UNIT_INFO_NAME)
    EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
