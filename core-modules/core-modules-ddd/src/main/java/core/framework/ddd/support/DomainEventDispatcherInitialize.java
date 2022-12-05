package core.framework.ddd.support;

import core.framework.ddd.DomainEventListener;
import core.framework.ddd.configuration.DomainEventDispatcherConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

/**
 * @author ebin
 */
public class DomainEventDispatcherInitialize implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        initializeDomainEventDispatcher(applicationContext);
        registerEventListener(applicationContext);
    }

    private void initializeDomainEventDispatcher(ApplicationContext applicationContext) {
        ThreadPoolTaskExecutor taskExecutor = applicationContext.getBean(DomainEventDispatcherConfiguration.DOMAIN_EVENT_TASK_EXECUTOR_NAME, ThreadPoolTaskExecutor.class);
        DomainEventDispatcher.INSTANCE.setTaskExecutor(taskExecutor);
    }

    @SuppressWarnings("rawtypes")
    private void registerEventListener(ApplicationContext applicationContext) {
        Map<String, DomainEventListener> listeners = applicationContext.getBeansOfType(DomainEventListener.class);
        listeners.forEach((k, v) -> DomainEventDispatcher.INSTANCE.registerEventListener(v));
    }
}
