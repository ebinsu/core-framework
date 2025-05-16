package core.framework.kernel.async;

import core.framework.kernel.log.marker.ErrorCodeMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.time.Duration;
import java.util.concurrent.Callable;

/**
 * @author ebin
 */
record ExecutionTask<T>(String action, Callable<T> callable, long maxProcessTimeInNano) implements Callable<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTask.class);

    @Override
    public T call() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        T result;
        try {
            result = callable.call();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        stopWatch.stop();
        long elapsed = stopWatch.getTotalTimeNanos();
        if (elapsed > maxProcessTimeInNano) {
            LOGGER.warn(new ErrorCodeMarker("SLOW_PROCESS"), "async task took longer than of max process time, maxProcessTime={}, elapsed={}", Duration.ofNanos(elapsed), Duration.ofNanos(elapsed));
        }
        return result;
    }
}
