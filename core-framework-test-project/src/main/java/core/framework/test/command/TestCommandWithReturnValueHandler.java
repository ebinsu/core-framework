package core.framework.test.command;

import core.framework.command.annotation.CommandHandler;
import org.springframework.stereotype.Service;

/**
 * @author ebin
 */
@Service
public class TestCommandWithReturnValueHandler {
    @CommandHandler
    public String handle(TestCommandWithReturnValue command) {
        return "abc";
    }
}
