package core.framework.kernel.async;

import core.framework.kernel.exception.ErrorCodeRuntimeException;
import core.framework.kernel.log.Logs;
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
        Logs.action("task:" + action);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        T result;
        try {
            result = callable.call();
        } catch (Exception e) {
            if (e instanceof ErrorCodeRuntimeException ex) {
                LOGGER.error(new ErrorCodeMarker(ex.errorCode()), e.getMessage(), e);
            } else {
                LOGGER.error(e.getMessage(), e);
            }
            throw e;
        }
        stopWatch.stop();
        long elapsed = stopWatch.getTotalTimeNanos();
        Logs.put("elapsed", elapsed);
        if (elapsed > maxProcessTimeInNano) {
            LOGGER.warn(new ErrorCodeMarker("SLOW_PROCESS"), "async task took longer than of max process time, maxProcessTime={}, elapsed={}", Duration.ofNanos(elapsed), Duration.ofNanos(elapsed));
        }
        return result;
    }
}
