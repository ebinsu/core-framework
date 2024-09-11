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
        String name = command.getClass().getName();
        InvocableCommandHandlerMethod invocableCommandHandlerMethod = commandHandlerMethods.get(name);
        if (invocableCommandHandlerMethod == null) {
            throw new RuntimeException("Command handle not found, Command name :" + name);
        }
        try {
            invocableCommandHandlerMethod.invoke(command);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dispatch(Object command, CommandCallback callback) {
        String name = command.getClass().getName();
        InvocableCommandHandlerMethod invocableCommandHandlerMethod = commandHandlerMethods.get(name);
        if (invocableCommandHandlerMethod == null) {
            throw new RuntimeException("Command handle not found, Command name :" + name);
        }
        try {
            Object result = invocableCommandHandlerMethod.invoke(command);
            callback.onResult(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void subscribe(InvocableCommandHandlerMethod invocableCommandHandlerMethod) {
        synchronized (this) {
            commandHandlerMethods.put(invocableCommandHandlerMethod.getCommandName(), invocableCommandHandlerMethod);
        }
    }
}
