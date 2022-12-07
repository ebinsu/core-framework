package core.framework.test;

import core.framework.command.CommandBus;
import core.framework.test.hibernate.command.CreatedTestDomainCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ebin
 */
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CommandTest {
    @Autowired
    CommandBus commandBus;

    @Test
    public void testCommandBus() {
        CreatedTestDomainCommand command = new CreatedTestDomainCommand();
        commandBus.dispatch(command);
        Assertions.assertTrue(command.handled);
    }
}
