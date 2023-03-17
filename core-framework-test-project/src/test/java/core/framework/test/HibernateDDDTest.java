package core.framework.test;

import core.framework.jpa.hibernate.DomainEventTracking;
import core.framework.test.hibernate.domain.AssignIdDomain;
import core.framework.test.hibernate.domain.AssignIdDomainEvent;
import core.framework.test.hibernate.domain.AssignIdEntity;
import core.framework.test.hibernate.domain.TestDomain;
import core.framework.test.hibernate.domain.TestDomainEvent;
import core.framework.test.hibernate.domain.TestDomainPreEvent;
import core.framework.test.hibernate.domain.TestDomainRepo;
import core.framework.test.hibernate.domain.TestEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ebin
 */
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class HibernateDDDTest {
    @Autowired
    TestDomainRepo testDomainRepo;
    @Autowired
    PlatformTransactionManager transactionManager;
    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testPersist1() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        AssignIdDomain assignIdDomain = new AssignIdDomain();
        assignIdDomain.abc = 1L;
        AssignIdEntity assignIdEntity = new AssignIdEntity();
        entityManager.persist(assignIdDomain);
        entityManager.persist(assignIdEntity);
        transactionManager.commit(status);
    }

    @Test
    public void testPersist2() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        AssignIdDomain assignIdDomain = new AssignIdDomain();
        assignIdDomain.abc = 1L;
        assignIdDomain.registerEvent(new AssignIdDomainEvent(assignIdDomain));
        entityManager.persist(assignIdDomain);
        transactionManager.commit(status);
    }

    @Test
    @Transactional
    public void testPersist() {
        TestDomain testDomain = new TestDomain("test");
        TestEntity testEntity = new TestEntity("test");
//        TestValueObject object = new TestValueObject("test");
        testDomain.setEntity(testEntity);
//        testDomain.setValueObject(object);
        testDomainRepo.persist(testDomain);
        Assertions.assertNotNull(testDomain.getId());
    }

    @Test
    public void testPreDomainEvent() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain testDomain = new TestDomain("test");
        TestDomainPreEvent testDomainPreEvent = new TestDomainPreEvent(testDomain);
        testDomain.registerEvent(testDomainPreEvent);
        testDomainRepo.persist(testDomain);
        transactionManager.commit(status);
        Assertions.assertTrue(testDomainPreEvent.handled);
    }

    @Test
    public void testPostDomainEvent() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain testDomain = new TestDomain("test");
        TestDomainEvent testDomainEvent = new TestDomainEvent(testDomain);
        TestEntity testEntity = new TestEntity("test");
        testDomain.setEntity(testEntity);
        testDomain.registerEvent(testDomainEvent);
        testDomainRepo.persist(testDomain);
        transactionManager.commit(status);

        status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        Query nativeQuery = entityManager.createNativeQuery("select * from domain_event_tracking", DomainEventTracking.class);
        List<DomainEventTracking> tracking = nativeQuery.getResultList();
        transactionManager.commit(status);
        Assertions.assertTrue(testDomainEvent.handled);
        Assertions.assertFalse(tracking.isEmpty());
    }

    @Test
    public void testNameQuery() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain testDomain = new TestDomain("test");
        testDomainRepo.persist(testDomain);
        transactionManager.commit(status);

        status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain test = testDomainRepo.findByQueryString("TestDomainFinder.selectByName", "test");
        List<TestDomain> tests = testDomainRepo.selectByQueryString("TestDomainFinder.selectByName", "test");
        transactionManager.commit(status);
        Assertions.assertNotNull(test.getId());
        Assertions.assertFalse(tests.isEmpty());
    }

    @Test
    public void testNativeQuery() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain testDomain = new TestDomain("test");
        testDomainRepo.persist(testDomain);
        transactionManager.commit(status);

        status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain test = testDomainRepo.findByQueryString("TestDomainFinder.selectById", testDomain.getId());
        List<TestDomain> tests = testDomainRepo.selectByQueryString("TestDomainFinder.selectById", testDomain.getId());
        transactionManager.commit(status);
        Assertions.assertNotNull(test.getId());
        Assertions.assertFalse(tests.isEmpty());
    }

    @Test
    public void testSqlQuery() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain testDomain = new TestDomain("test");
        testDomainRepo.persist(testDomain);
        transactionManager.commit(status);

        status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain test = testDomainRepo.findByQueryString("select * from test where id = ?1", testDomain.getId());
        List<TestDomain> tests = testDomainRepo.selectByQueryString("select * from test where id = ?1", testDomain.getId());
        transactionManager.commit(status);
        Assertions.assertNotNull(test.getId());
        Assertions.assertFalse(tests.isEmpty());
    }

    @Test
    public void testAggregateByNativeQuery() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain testDomain = new TestDomain("test");
        testDomainRepo.persist(testDomain);
        transactionManager.commit(status);
        Integer count = testDomainRepo.aggregateByQueryString("select count(*) from test", Integer.class);
        Assertions.assertEquals(count, 1);
    }

    @Test
    public void testAggregateByNameQuery() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain testDomain = new TestDomain("test");
        testDomainRepo.persist(testDomain);
        transactionManager.commit(status);
        Long count = testDomainRepo.aggregateByQueryString("TestDomainFinder.count1", Long.class);
        Assertions.assertEquals(count, 1);
    }

    @Test
    public void testAggregateByNativeNameQuery() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain testDomain = new TestDomain("test");
        testDomainRepo.persist(testDomain);
        transactionManager.commit(status);
        Long count = testDomainRepo.aggregateByQueryString("TestDomainFinder.count2", Long.class);
        Assertions.assertEquals(count, 1);
    }
}
