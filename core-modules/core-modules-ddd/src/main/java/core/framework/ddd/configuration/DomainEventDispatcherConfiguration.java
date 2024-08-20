package core.framework.ddd.configuration;

import core.framework.ddd.DomainEventBus;
import core.framework.ddd.support.DomainEventBusHolder;
import core.framework.ddd.support.DomainEventBusImpl;
import core.framework.shared.async.ExtendThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Import(DomainEventHandlerBeanDefinitionRegistrar.class)
public class DomainEventDispatcherConfiguration {
    public static final String DOMAIN_EVENT_TASK_EXECUTOR_NAME = "domainEventTaskExecutor";
    private static final int DOMAIN_EVENT_TASK_EXECUTOR_AWAIT_TERMINATION_SECONDS = 60 * 2;

    @Bean
    public DomainEventBus domainEventBusInitialize(ThreadPoolTaskExecutor domainEventTaskExecutor) {
        return new DomainEventBusImpl(domainEventTaskExecutor);
    }

    @Bean
    public DomainEventBusHolder domainEventBusHolder() {
        return new DomainEventBusHolder();
    }

    @Bean(name = DOMAIN_EVENT_TASK_EXECUTOR_NAME)
    public Executor domainEventTaskExecutor() {
        ExtendThreadPoolTaskExecutor taskExecutor = new ExtendThreadPoolTaskExecutor();
        taskExecutor.setThreadPriority(Thread.MAX_PRIORITY);
        taskExecutor.setThreadGroupName("domain-event-thread-executor");
        taskExecutor.setThreadNamePrefix("domain-event-thread");
        taskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors() + 1);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(DOMAIN_EVENT_TASK_EXECUTOR_AWAIT_TERMINATION_SECONDS);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
