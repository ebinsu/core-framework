package core.framework.jpa.hibernate.support;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

/**
 * @author ebin
 */
public class DomainEventTrackingPersistentUnitHolderInitializer implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        initializeDomainEventTrackingPersistentUnitHolder(applicationContext);
    }

    private void initializeDomainEventTrackingPersistentUnitHolder(ApplicationContext applicationContext) {
        Map<String, EntityManagerFactory> entityManagerFactories = applicationContext.getBeansOfType(EntityManagerFactory.class);
        DomainEventTrackingPersistentUnitHolder.INSTANCE.setManagerFactories(entityManagerFactories.values());
    }
}
