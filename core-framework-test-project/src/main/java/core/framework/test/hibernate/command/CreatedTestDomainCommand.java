package core.framework.test.hibernate.command;

import core.framework.command.Command;

/**
 * @author ebin
 */
public class CreatedTestDomainCommand implements Command<Void> {
    public boolean handled;
}
