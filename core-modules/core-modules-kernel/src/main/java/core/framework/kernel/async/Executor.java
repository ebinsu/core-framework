package core.framework.kernel.async;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author ebin
 */
public interface Executor {
    <T> Future<T> submit(String action, Callable<T> task);

    Future<Void> submit(String action, Runnable runnable);

    void submit(String action, Runnable runnable, Duration delay);
}
