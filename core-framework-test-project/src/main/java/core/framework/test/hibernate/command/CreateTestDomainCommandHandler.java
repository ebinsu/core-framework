package core.framework.test.hibernate.command;

import core.framework.command.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ebin
 */
@Service
public class CreateTestDomainCommandHandler implements CommandHandler<CreatedTestDomainCommand, Void> {

    @Transactional
    @Override
    public Void handle(CreatedTestDomainCommand command) {
        command.handled = true;
        return null;
    }
}
