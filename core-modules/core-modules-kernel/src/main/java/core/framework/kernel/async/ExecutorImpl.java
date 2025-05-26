package core.framework.kernel.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static core.framework.kernel.utils.Markers.errorCode;

/**
 * @author ebin
 */
public class ExecutorImpl implements Executor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorImpl.class);

    private final ExecutorService executor;
    private final long maxProcessTimeInNano = Duration.ofSeconds(10).toNanos();
    private final ReentrantLock lock = new ReentrantLock();
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
        lock.lock();
        try {
            if (executor.isShutdown()) {
                LOGGER.warn(errorCode("TASK_REJECTED"), "reject task due to server is shutting down, action={}", action);    // with current executor impl, rejection only happens when shutdown
                return;
            }
            if (scheduler == null) {
                scheduler = Executors.singleThreadScheduler("executor-scheduler-");
            }
        } finally {
            lock.unlock();
        }
        scheduleDelayedTask(action, runnable, delay);
    }

    protected <T> Future<T> submitTask(ExecutionTask<T> executionTask) {
        try {
            return executor.submit(executionTask);
        } catch (RejectedExecutionException e) {
            LOGGER.warn(errorCode("TASK_REJECTED"), "reject task due to server is shutting down, action={}", executionTask.action(), e);
            return new CancelledFuture<>();
        }
    }

    protected void scheduleDelayedTask(String action, Runnable runnable, Duration delay) {
        ExecutionTask<Void> execution = new ExecutionTask<>(action, new CallableAdaptor(runnable), maxProcessTimeInNano);
        try {
            scheduler.schedule(new DelayedTask(execution), delay.toMillis(), TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            LOGGER.warn(errorCode("TASK_REJECTED"), "reject task due to server is shutting down, action={}", action, e);
        }
    }

    class DelayedTask implements Callable<Void> {
        final ExecutionTask<Void> execution;

        DelayedTask(ExecutionTask<Void> execution) {
            this.execution = execution;
        }

        @Override
        public Void call() {
            submitTask(execution);
            return null;
        }

        // used to print all canceled tasks during shutdown
        @Override
        public String toString() {
            return execution.toString();
        }
    }
}
