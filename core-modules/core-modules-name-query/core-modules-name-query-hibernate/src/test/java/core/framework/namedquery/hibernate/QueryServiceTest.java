package core.framework.namedquery.hibernate;

import core.framework.namedquery.NamedQueryService;
import core.framework.namedquery.configuration.NamedQueryConfiguration;
import core.framework.namedquery.hibernate.configuration.HibernateQueryServiceConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
@SpringBootTest(classes = {
    TestConfig.class,
    NamedQueryConfiguration.class,
    HibernateQueryServiceConfiguration.class,
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class QueryServiceTest {
    @Autowired
    NamedQueryService namedQueryService;
    @Autowired
    PlatformTransactionManager transactionManager;
    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void test() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        entityManager.persist(new TestEntity("test"));
        transactionManager.commit(status);
        List<Map> select = namedQueryService.select("test.1", Map.of("name", "test"));
        Assertions.assertFalse(select.isEmpty());
    }
}
