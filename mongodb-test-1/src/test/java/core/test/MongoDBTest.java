package core.test;

import core.framework.jpa.eclipselink.mongodb.configuration.MongodbConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * @author ebin
 */
@SpringBootTest
public class MongoDBTest {
    @PersistenceContext(unitName = "mongodb")
    EntityManager entityManager;

    @Autowired
    @Qualifier(MongodbConfiguration.MONGODB_TRANSACTION_MANAGER_BEAN_NAME)
    PlatformTransactionManager transactionManager;

    @Test
    @Rollback(value = false)
    public void test1() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain testDomain = new TestDomain("test");
        testDomain.registerEvent(new TestDomainEvent(testDomain));
        System.out.println(testDomain.getId());
        entityManager.persist(testDomain);
        System.out.println(testDomain.getId());
        transactionManager.commit(status);
        System.out.println(testDomain.getId());
    }

    @Test
    @Rollback(value = false)
    public void test2() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        Order o = new Order();
        entityManager.persist(o);
        System.out.println(o.getId());
        transactionManager.commit(status);
        System.out.println(o.getId());
    }


    public static void main(String[] args) {
//        EntityManagerFactory entityManagerFactory =
//            Persistence.createEntityManagerFactory("mongodb");
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//        entityManager.getTransaction().begin();
//
//        Order o = new Order();
//        entityManager.persist(o);
//        entityManager.getTransaction().commit();
//
//        entityManager.close();
//        entityManagerFactory.close();
    }
}
