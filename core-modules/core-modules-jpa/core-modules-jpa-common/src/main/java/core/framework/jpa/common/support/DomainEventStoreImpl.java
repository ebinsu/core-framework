package core.framework.jpa.common.support;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.ddd.support.AbstractDomainEvent;
import core.framework.exception.marker.ErrorCodeMarker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public final class DomainEventStoreImpl implements core.framework.ddd.DomainEventStore {
    public static final DomainEventStoreImpl INSTANCE = new DomainEventStoreImpl();
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventStoreImpl.class);

    private Map<String, EntityManager> entityManagers;
    private Map<Class<?>, String> aggregateRootPersistenceType;
    private Map<String, DomainEventPersistenceDriver> persistenceDrivers;

    private DomainEventStoreImpl() {
    }

    public void setManagerFactories(Collection<EntityManagerFactory> entityManagerFactories, Collection<DomainEventPersistenceDriver> values) {
        this.entityManagers = new HashMap<>(entityManagerFactories.size());
        this.aggregateRootPersistenceType = new HashMap<>();
        this.persistenceDrivers = values.stream().collect(Collectors.toMap(DomainEventPersistenceDriver::getPersistenceUnitName, Function.identity()));

        entityManagerFactories.forEach(entityManagerFactory -> {
            EntityManager entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
            String persistenceUnitName = entityManagerFactory.toString();
            this.entityManagers.put(persistenceUnitName, entityManager);

            entityManager.getMetamodel().getEntities().forEach(entityType -> {
                Class<?> javaType = entityType.getJavaType();
                if (AggregateRoot.class.isAssignableFrom(javaType)) {
                    this.aggregateRootPersistenceType.put(javaType, persistenceUnitName);
                }
            });
        });
    }

    @Override
    public void persist(AggregateRoot<?, ?> aggregateRoot) {
        aggregateRoot.prepareDispatchDomainEvent();
        List<? extends DomainEvent<?, ?>> domainEvents = aggregateRoot.getDomainEvents();
        if (domainEvents.isEmpty()) {
            return;
        }
        String persistenceUnitName = aggregateRootPersistenceType.get(aggregateRoot.getClass());
        if (persistenceUnitName != null) {
            EntityManager entityManager = entityManagers.get(persistenceUnitName);
            for (DomainEvent<?, ?> domainEvent : domainEvents) {
                if (domainEvent instanceof AbstractDomainEvent) {
                    DomainEventPersistenceDriver domainEventPersistenceDriver = persistenceDrivers.get(persistenceUnitName);
                    if (domainEventPersistenceDriver == null) {
                        LOGGER.warn(new ErrorCodeMarker("PERSISTENCE_DRIVER_NOT_FOUND"), "Domain event persistence driver not found, skip persistence domain event.");
                    } else {
                        domainEventPersistenceDriver.persist(aggregateRoot, entityManager);
                    }
                }
            }
        }
    }
}
