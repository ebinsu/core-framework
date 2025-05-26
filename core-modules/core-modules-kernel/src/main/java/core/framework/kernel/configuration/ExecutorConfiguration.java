package core.framework.kernel.configuration;

import core.framework.kernel.async.Executor;
import core.framework.kernel.async.ExecutorImpl;
import core.framework.kernel.async.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class ExecutorConfiguration {
    @Bean
    public Executor executor() {
        return new ExecutorImpl(Executors.virtualThreadExecutor("executor-"));
    }
}
