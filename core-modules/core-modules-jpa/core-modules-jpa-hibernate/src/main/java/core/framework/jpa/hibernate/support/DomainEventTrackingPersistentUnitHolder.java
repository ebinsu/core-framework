package core.framework.jpa.hibernate.support;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.jpa.hibernate.AbstractDomainEvent;
import core.framework.jpa.hibernate.DomainEventTracking;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
public final class DomainEventTrackingPersistentUnitHolder {
    public static final DomainEventTrackingPersistentUnitHolder INSTANCE = new DomainEventTrackingPersistentUnitHolder();

    private Map<String, EntityManager> entityManagers;
    private Map<Class<?>, String> aggregateRootPersistenceType;

    private DomainEventTrackingPersistentUnitHolder() {
    }

    public void setManagerFactories(Collection<EntityManagerFactory> entityManagerFactories) {
        this.entityManagers = new HashMap<>(entityManagerFactories.size());
        this.aggregateRootPersistenceType = new HashMap<>();

        entityManagerFactories.forEach(entityManagerFactory -> {
            EntityManager entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
            String persistenceUnitName = (String) entityManagerFactory.getProperties().get("hibernate.ejb.persistenceUnitName");
            this.entityManagers.put(persistenceUnitName, entityManager);

            entityManager.getMetamodel().getEntities().forEach(entityType -> {
                Class<?> javaType = entityType.getJavaType();
                if (AggregateRoot.class.isAssignableFrom(javaType)) {
                    this.aggregateRootPersistenceType.put(javaType, persistenceUnitName);
                }
            });
        });
    }

    public void persist(AggregateRoot<?> aggregateRoot) {
        List<? extends DomainEvent<?>> domainEvents = aggregateRoot.getDomainEvents();
        if (domainEvents.isEmpty()) {
            return;
        }
        String persistenceUnitName = aggregateRootPersistenceType.get(aggregateRoot.getClass());
        if (persistenceUnitName != null) {
            EntityManager entityManager = entityManagers.get(persistenceUnitName);
            for (DomainEvent<?> domainEvent : domainEvents) {
                if (domainEvent instanceof AbstractDomainEvent) {
                    DomainEventTracking domainEventTracking = new DomainEventTracking((AbstractDomainEvent<?>) domainEvent);
                    entityManager.persist(domainEventTracking);
                }
            }
        }
    }

}
