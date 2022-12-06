package core.framework.test;

import core.framework.test.hibernate.domain.TestDomain;
import core.framework.test.hibernate.domain.TestDomainEvent;
import core.framework.test.hibernate.domain.TestDomainPreEvent;
import core.framework.test.hibernate.domain.TestDomainRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ebin
 */
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class DDDHibernateTest {
    @Autowired
    TestDomainRepo testDomainRepo;
    @Autowired
    PlatformTransactionManager transactionManager;

    @Test
    @Transactional
    public void testPersist() {
        TestDomain testDomain = new TestDomain("test");
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
        testDomain.registerEvent(testDomainEvent);
        testDomainRepo.persist(testDomain);
        transactionManager.commit(status);
        Assertions.assertTrue(testDomainEvent.handled);
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
        TestDomain test = testDomainRepo.findByQueryString("TestDomainFinder.selectById", 1);
        List<TestDomain> tests = testDomainRepo.selectByQueryString("TestDomainFinder.selectById", 1);
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
        TestDomain test = testDomainRepo.findByQueryString("select * from test where id = ?1", 1);
        List<TestDomain> tests = testDomainRepo.selectByQueryString("select * from test where id = ?1", 1);
        transactionManager.commit(status);
        Assertions.assertNotNull(test.getId());
        Assertions.assertFalse(tests.isEmpty());
    }
}
