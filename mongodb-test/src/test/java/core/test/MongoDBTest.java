package core.test;

import core.test.mongo.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
    PlatformTransactionManager transactionManager;

    @Test
    @Rollback(value = false)
    public void test1() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        try {
            Order o1 = new Order();
            o1.add("1");
            entityManager.persist(o1);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
        }

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
