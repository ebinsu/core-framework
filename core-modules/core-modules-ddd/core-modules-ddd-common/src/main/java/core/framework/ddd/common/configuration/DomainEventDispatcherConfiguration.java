package core.framework.ddd.common.configuration;

import core.framework.ddd.api.DomainEventBus;
import core.framework.ddd.common.event.DomainEventBusHolder;
import core.framework.ddd.common.event.DomainEventBusImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.VirtualThreadTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@Import(DomainEventHandlerBeanDefinitionRegistrar.class)
public class DomainEventDispatcherConfiguration {
    public static final String DOMAIN_EVENT_TASK_EXECUTOR_NAME = "domainEventTaskExecutor";
    private static final int DOMAIN_EVENT_TASK_EXECUTOR_AWAIT_TERMINATION_SECONDS = 60 * 2;

    @Bean
    public DomainEventBus domainEventBusInitialize(Executor domainEventTaskExecutor) {
        return new DomainEventBusImpl(domainEventTaskExecutor);
    }

    @Bean
    public DomainEventBusHolder domainEventBusHolder() {
        return new DomainEventBusHolder();
    }

    @Bean(name = DOMAIN_EVENT_TASK_EXECUTOR_NAME)
    public Executor domainEventTaskExecutor() {
        return new VirtualThreadTaskExecutor("domain-event");
    }
}
