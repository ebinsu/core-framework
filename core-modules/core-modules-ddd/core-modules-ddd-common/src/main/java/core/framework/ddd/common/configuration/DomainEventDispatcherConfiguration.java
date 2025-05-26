package core.framework.ddd.common.configuration;

import core.framework.ddd.api.DomainEventBus;
import core.framework.ddd.common.event.DomainEventBusHolder;
import core.framework.ddd.common.event.DomainEventBusImpl;
import core.framework.kernel.async.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import(DomainEventHandlerBeanDefinitionRegistrar.class)
public class DomainEventDispatcherConfiguration {
    @Bean
    public DomainEventBus domainEventBusInitialize(Executor executor) {
        return new DomainEventBusImpl(executor);
    }

    @Bean
    public DomainEventBusHolder domainEventBusHolder() {
        return new DomainEventBusHolder();
    }
}
