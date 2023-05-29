package core.test;

import core.framework.jpa.hibernate.mysql.configuration.HibernateConfiguration;
import core.test.mysql.TestEntity;
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
public class MysqlTest {
    @PersistenceContext(unitName = "mysql")
    EntityManager entityManager;

    @Autowired
    @Qualifier(HibernateConfiguration.MYSQL_TRANSACTION_MANAGER_BEAN_NAME)
    PlatformTransactionManager transactionManager;

    @Test
    @Rollback(value = false)
    public void test1() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestEntity testDomain = new TestEntity("test");
        entityManager.persist(testDomain);
        transactionManager.commit(status);
        System.out.println(testDomain.getId());
    }
}
