package app.service;

import core.framework.exception.ErrorCodeRuntimeException;
import core.framework.kernel.async.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            throw new ErrorCodeRuntimeException("TEST", "test");
        });
    }
}
