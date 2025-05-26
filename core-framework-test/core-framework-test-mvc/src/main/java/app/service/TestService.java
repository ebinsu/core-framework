package app.service;

import core.framework.kernel.async.Executor;
import core.framework.kernel.exception.ErrorCodeRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @author ebin
 */
@Service
public class TestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestService.class);
    @Autowired
    Executor threadPoolTaskExecutor;

    public void test(String userId) {
        threadPoolTaskExecutor.submit("test", () -> {
            LOGGER.info(userId);
            LOGGER.info(Thread.currentThread().getName());

            throw new ErrorCodeRuntimeException("TEST", "test");
        });
        threadPoolTaskExecutor.submit("test-1s", () -> {
            LOGGER.info(userId);
            LOGGER.info(Thread.currentThread().getName());

            throw new ErrorCodeRuntimeException("TEST-1s", "test-1s");
        }, Duration.ofSeconds(1));
    }
}
