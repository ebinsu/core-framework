package core.framework.test.hibernate.command;

import core.framework.command.annotation.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ebin
 */
@Service
public class CreateTestDomainCommandHandler {

    @Transactional
    @CommandHandler
    public void handle(CreatedTestDomainCommand command) {
        command.handled = true;
    }
}
