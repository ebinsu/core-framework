package core.test.mongo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

import java.util.Map;

/**
 * @author ebin
 */
public class EntityManagerHolder implements ApplicationContextAware {
    private static EntityManager entityManager;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (entityManager == null) {
            EntityManagerFactory entityManagerFactory = applicationContext.getBean(EntityManagerFactory.class);
            EntityManagerHolder.entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
        }
    }

    public static EntityManager get() throws BeansException {
        return EntityManagerHolder.entityManager;
    }
}

