package core.framework.kernel.async;

import core.framework.kernel.utils.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author ebin
 */
public class ExecutorImpl implements Executor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorImpl.class);

    private final ExecutorService executor;
    private final long maxProcessTimeInNano = Duration.ofSeconds(10).toNanos();

    private ScheduledExecutorService scheduler;

    public ExecutorImpl(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public <T> Future<T> submit(String action, Callable<T> callable) {
        ExecutionTask<T> executionTask = new ExecutionTask<>(action, callable, maxProcessTimeInNano);
        return submitTask(executionTask);
    }

    @Override
    public Future<Void> submit(String action, Runnable runnable) {
        CallableAdaptor callable = new CallableAdaptor(runnable);
        ExecutionTask<Void> executionTask = new ExecutionTask<>(action, callable, maxProcessTimeInNano);
        return submitTask(executionTask);
    }

    @Override
    public void submit(String action, Runnable runnable, Duration delay) {

    }

    private <T> Future<T> submitTask(ExecutionTask<T> executionTask) {
        try {
            return executor.submit(executionTask);
        } catch (RejectedExecutionException e) {
            LOGGER.warn(Markers.errorCode("TASK_REJECTED"), "reject task due to server is shutting down, action={}", executionTask.action(), e);
            return new CancelledFuture<>();
        }
    }
}
