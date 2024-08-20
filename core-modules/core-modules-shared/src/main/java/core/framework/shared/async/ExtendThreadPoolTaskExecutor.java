package core.framework.shared.async;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author ebin
 */
public class ExtendThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    public static final long MAX_PROCESS_TIME_IN_NANO = Duration.ofSeconds(10).toNanos();

    @Override
    public void execute(Runnable task, long startTimeout) {
        super.submit(new RunnableAdaptor(task));
    }

    @Override
    public void execute(Runnable task) {
        super.submit(new RunnableAdaptor(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(new RunnableAdaptor(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(new CallableAdaptor<>(task));
    }

    @Override
    public CompletableFuture<Void> submitCompletable(Runnable task) {
        return super.submitCompletable(new RunnableAdaptor(task));
    }

    @Override
    public <T> CompletableFuture<T> submitCompletable(Callable<T> task) {
        return super.submitCompletable(new CallableAdaptor<>(task));
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        return super.submitListenable(new RunnableAdaptor(task));
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return super.submitListenable(new CallableAdaptor<>(task));
    }
}
