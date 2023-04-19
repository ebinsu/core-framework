package core.framework.command.support;

import core.framework.command.CommandBus;
import core.framework.command.CommandCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public class CommandBusImpl implements CommandBus {
    private final Map<String, InvocableCommandHandlerMethod> commandHandlerMethods = new HashMap<>();

    @Override
    public void dispatch(Object command) {
        InvocableCommandHandlerMethod invocableCommandHandlerMethod = commandHandlerMethods.get(command.getClass().getName());
        try {
            invocableCommandHandlerMethod.invoke(command);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dispatch(Object command, CommandCallback callback) {
        InvocableCommandHandlerMethod invocableCommandHandlerMethod = commandHandlerMethods.get(command.getClass().getName());
        try {
            Object result = invocableCommandHandlerMethod.invoke(command);
            callback.onResult(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(InvocableCommandHandlerMethod invocableCommandHandlerMethod) {
        synchronized (this) {
            commandHandlerMethods.put(invocableCommandHandlerMethod.getCommandName(), invocableCommandHandlerMethod);
        }
    }
}
