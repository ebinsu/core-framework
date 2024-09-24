package core.framework.ddd.hibernate.internal;

import core.framework.ddd.api.AggregateRoot;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public final class EntityManagerHolder {
    public static final EntityManagerHolder INSTANCE = new EntityManagerHolder();

    private Map<String, EntityManager> entityManagers;
    private Map<Class<?>, String> aggregateRootPersistenceType;

    private EntityManagerHolder() {
    }

    public void setManagerFactories(Collection<EntityManagerFactory> entityManagerFactories) {
        this.entityManagers = new HashMap<>(entityManagerFactories.size());
        this.aggregateRootPersistenceType = new HashMap<>();

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

    public String getPersistenceUnitName(Class<? extends AggregateRoot> aggClass) {
        return aggregateRootPersistenceType.get(aggClass);
    }

    public EntityManager get(String persistenceUnitName) {
        return entityManagers.get(persistenceUnitName);
    }
}
