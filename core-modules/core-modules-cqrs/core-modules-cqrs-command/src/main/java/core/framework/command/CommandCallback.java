package core.framework.command;

/**
 * @author ebin
 */
@FunctionalInterface
public interface CommandCallback {
    void onResult(Object commandResultMessage);
}
