package core.framework.shared.async;

import core.framework.exception.marker.ErrorCodeMarker;
import core.framework.shared.utils.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Callable;

import static core.framework.shared.async.ExtendThreadPoolTaskExecutor.MAX_PROCESS_TIME_IN_NANO;

/**
 * @author ebin
 */
class CallableAdaptor<T> implements Callable<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallableAdaptor.class);
    private final Callable<T> target;

    CallableAdaptor(Callable<T> target) {
        this.target = target;
    }

    @Override
    public T call() throws Exception {
        StopWatch stopWatch = new StopWatch();
        T result = target.call();
        long elapsed = stopWatch.elapsed();
        if (elapsed > MAX_PROCESS_TIME_IN_NANO) {
            LOGGER.warn(new ErrorCodeMarker("SLOW_PROCESS"), "async task took longer than of max process time, maxProcessTime={}, elapsed={}", Duration.ofNanos(MAX_PROCESS_TIME_IN_NANO), Duration.ofNanos(elapsed));
        }
        return result;
    }
}
