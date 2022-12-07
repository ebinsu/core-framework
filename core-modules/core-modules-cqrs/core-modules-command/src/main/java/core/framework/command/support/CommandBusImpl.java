package core.framework.command.support;

import core.framework.command.Command;
import core.framework.command.CommandBus;
import core.framework.command.CommandHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public class CommandBusImpl implements CommandBus {
    private final Map<Class<? extends Command<?>>, CommandHandler<? extends Command<?>, ?>> commandHandlers = new HashMap<>();

    @Override
    public <T> T dispatch(Command<T> command) {
        CommandHandler<Command<T>, T> handler = (CommandHandler<Command<T>, T>) commandHandlers.get(command.getClass());
        if (handler == null) {
            throw new RuntimeException("Command handler not found!");
        }
        return handler.handle(command);
    }

    @Override
    public void register(CommandHandler<? extends Command<?>, ?> handler) {
        Type genericSuperclass = handler.getClass().getGenericSuperclass();
        Type[] interfaces;
        if (genericSuperclass instanceof Class<?>
                && CommandHandler.class.isAssignableFrom((Class<?>) genericSuperclass)) {
            interfaces = ((Class<?>) genericSuperclass).getGenericInterfaces();
        } else {
            interfaces = handler.getClass().getGenericInterfaces();
        }
        ParameterizedType handlerInterface = Arrays.stream(interfaces).filter(type -> CommandHandler.class == ((ParameterizedType) type).getRawType())
                .findFirst().map(m -> (ParameterizedType) m)
                .orElseThrow();
        Type[] actualTypeArguments = handlerInterface.getActualTypeArguments();
        if (actualTypeArguments.length > 0) {
            commandHandlers.put((Class<? extends Command<?>>) handlerInterface.getActualTypeArguments()[0], handler);
        }
    }
}
