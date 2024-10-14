package core.framework.ddd.hibernate;

import core.framework.ddd.common.configuration.DomainEventDispatcherConfiguration;
import core.framework.ddd.hibernate.configuration.HibernateConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.math.BigInteger;
import java.util.List;

/**
 * @author ebin
 */
@SpringBootTest(classes = {
    Context.class,
    HibernateConfiguration.class,
    DomainEventDispatcherConfiguration.class,
    DataSourceAutoConfiguration.class
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class HibernateTest {
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    TestEntityRepo testEntityRepo;
    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void test() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestEntity test = new TestEntity("test");
        CreatedEvent createdEvent = new CreatedEvent();
        test.registerEvent(createdEvent);
        entityManager.flush();
        entityManager.flush();
        testEntityRepo.persist(test);
        transactionManager.commit(status);

        BigInteger entityCount = (BigInteger) entityManager.createNativeQuery("select count(*) from test", BigInteger.class).getSingleResult();
        BigInteger trackingCount = (BigInteger) entityManager.createNativeQuery("select count(*) from domain_event_tracking", BigInteger.class).getSingleResult();
        Assertions.assertEquals(1, entityCount.bitCount());
        Assertions.assertEquals(1, trackingCount.bitCount());
        Assertions.assertTrue(createdEvent.handle);
        Assertions.assertNotNull(createdEvent.getAggregateRootMetadata());
    }

    @Test
    public void testQuery() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestEntity test = new TestEntity("test");
        testEntityRepo.persist(test);
        transactionManager.commit(status);

        List<TestEntity> results = testEntityRepo.select((qb, root) -> List.of(qb.equal(root.get("id"), test.getId())));
        Assertions.assertEquals(1, results.size());
    }
}
