package core.framework.test;

import core.framework.command.CommandBus;
import core.framework.test.command.TestCommand;
import core.framework.test.command.TestCommandWithReturnValue;
import core.framework.test.hibernate.command.CreatedTestDomainCommand;
import core.framework.test.hibernate.domain.AssignIdDomain;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ebin
 */
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CommandTest {
    @Autowired
    CommandBus commandBus;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    PlatformTransactionManager transactionManager;

    @Test
    void testCommandBus() {
        CreatedTestDomainCommand command = new CreatedTestDomainCommand();
        commandBus.dispatch(command);
        Assertions.assertTrue(command.handled);
    }

    @Test
    @Rollback(value = false)
    void testCommand() {
        commandBus.dispatch(new TestCommand());
        TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        AssignIdDomain assignIdDomain = new AssignIdDomain();
        entityManager.find(AssignIdDomain.class, 1L);
        transactionManager.commit(status);
        Assertions.assertNotNull(assignIdDomain);
    }

    @Test
    void testCommandWithReturnValue() {
        AtomicReference<String> x = new AtomicReference<>(null);
        commandBus.dispatch(new TestCommandWithReturnValue(), result -> x.set((String) result));
        Assertions.assertNotNull(x.get());
    }
}
