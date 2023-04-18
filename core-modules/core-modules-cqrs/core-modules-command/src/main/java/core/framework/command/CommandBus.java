package core.framework.command;

import core.framework.command.support.InvocableCommandHandlerMethod;
import jakarta.annotation.Nonnull;

/**
 * @author ebin
 */
public interface CommandBus {
    void dispatch(@Nonnull Object command);

    void dispatch(@Nonnull Object command, @Nonnull CommandCallback callback);

    void subscribe(@Nonnull InvocableCommandHandlerMethod invocableCommandHandlerMethod);
}
