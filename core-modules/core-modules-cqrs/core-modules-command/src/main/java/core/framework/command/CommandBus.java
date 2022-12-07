package core.framework.command;

/**
 * @author ebin
 */
public interface CommandBus {
    <T> T dispatch(Command<T> query);

    void register(CommandHandler<? extends Command<?>, ?> handler);
}
