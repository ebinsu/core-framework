package core.framework.shared.async;

import core.framework.exception.marker.ErrorCodeMarker;
import core.framework.shared.utils.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static core.framework.shared.async.ExtendThreadPoolTaskExecutor.MAX_PROCESS_TIME_IN_NANO;

/**
 * @author ebin
 */
class RunnableAdaptor implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunnableAdaptor.class);
    private final Runnable target;

    RunnableAdaptor(Runnable target) {
        this.target = target;
    }

    @Override
    public void run() {
        StopWatch stopWatch = new StopWatch();
        target.run();
        long elapsed = stopWatch.elapsed();
        if (elapsed > MAX_PROCESS_TIME_IN_NANO) {
            LOGGER.warn(new ErrorCodeMarker("SLOW_PROCESS"), "async task took longer than of max process time, maxProcessTime={}, elapsed={}", Duration.ofNanos(MAX_PROCESS_TIME_IN_NANO), Duration.ofNanos(elapsed));
        }
    }
}
