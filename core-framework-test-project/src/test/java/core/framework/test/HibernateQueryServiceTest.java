package core.framework.test;

import core.framework.query.QueryBus;
import core.framework.query.support.QueryService;
import core.framework.test.hibernate.domain.TestDomain;
import core.framework.test.hibernate.domain.TestDomainRepo;
import core.framework.test.hibernate.query.GetTestDomainQuery;
import core.framework.test.hibernate.query.GetTestDomainQueryParam;
import core.framework.test.hibernate.query.ListTestDomainQuery;
import core.framework.test.hibernate.query.TestDomainDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class HibernateQueryServiceTest {
    @Autowired
    QueryService queryService;
    @Autowired
    TestDomainRepo testDomainRepo;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    QueryBus queryBus;

    @Test
    public void test() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain testDomain = new TestDomain("test");
        testDomainRepo.persist(testDomain);
        transactionManager.commit(status);
        GetTestDomainQueryParam getTestDomainCommand = new GetTestDomainQueryParam();
        getTestDomainCommand.addQueryParam("id", testDomain.getId());
        Optional<TestDomainDTO> testDomainDTOOptional = queryService.get(getTestDomainCommand);
        Assertions.assertTrue(testDomainDTOOptional.isPresent());
        TestDomainDTO testDomainDTO = testDomainDTOOptional.get();
        Assertions.assertNotNull(testDomainDTO.id);
        Assertions.assertNotNull(testDomainDTO.name);

        List<TestDomainDTO> select = queryService.select(getTestDomainCommand);
        Assertions.assertFalse(select.isEmpty());
        testDomainDTO = select.get(0);
        Assertions.assertNotNull(testDomainDTO.id);
        Assertions.assertNotNull(testDomainDTO.name);
    }

    @Test
    public void testQueryBus() {
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        TestDomain testDomain = new TestDomain("test");
        testDomainRepo.persist(testDomain);
        transactionManager.commit(status);

        GetTestDomainQuery getTestDomainQuery = new GetTestDomainQuery();
        TestDomainDTO testDomainDTO = queryBus.dispatch(getTestDomainQuery);
        Assertions.assertNotNull(testDomainDTO.id);
        Assertions.assertNotNull(testDomainDTO.name);

        ListTestDomainQuery query = new ListTestDomainQuery();
        List<TestDomainDTO> result = queryBus.dispatch(query);
        Assertions.assertFalse(result.isEmpty());
        testDomainDTO = result.get(0);
        Assertions.assertNotNull(testDomainDTO.id);
        Assertions.assertNotNull(testDomainDTO.name);
    }
}
