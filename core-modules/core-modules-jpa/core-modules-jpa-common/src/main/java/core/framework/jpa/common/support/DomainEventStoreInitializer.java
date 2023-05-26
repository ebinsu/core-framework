package core.framework.jpa.common.support;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

/**
 * @author ebin
 */
public class DomainEventStoreInitializer implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        initializeDomainEventTrackingPersistentUnitHolder(applicationContext);
    }

    private void initializeDomainEventTrackingPersistentUnitHolder(ApplicationContext applicationContext) {
        Map<String, EntityManagerFactory> entityManagerFactories = applicationContext.getBeansOfType(EntityManagerFactory.class);
        Map<String, DomainEventPersistenceDriver> domainEventPersistenceDrivers = applicationContext.getBeansOfType(DomainEventPersistenceDriver.class);
        DomainEventStoreImpl.INSTANCE.setManagerFactories(entityManagerFactories.values(), domainEventPersistenceDrivers.values());
    }
}
