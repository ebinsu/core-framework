package core.framework.ddd.common.event;

import core.framework.ddd.api.DomainEventBus;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author ebin
 */
public class DomainEventBusHolder implements ApplicationContextAware {
    private static DomainEventBus domainEventBus;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (domainEventBus == null) {
            DomainEventBusHolder.domainEventBus = applicationContext.getBean(DomainEventBus.class);
        }
    }

    public static DomainEventBus get() throws BeansException {
        return DomainEventBusHolder.domainEventBus;
    }
}
