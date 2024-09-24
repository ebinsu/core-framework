package core.framework.ddd.hibernate.internal.event;

import core.framework.ddd.hibernate.DomainEventStorageImpl;
import core.framework.ddd.hibernate.HibernateDomainEventDispatcher;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * @author ebin
 */
public class DomainEventIntegrator implements Integrator {

    @Override
    public void integrate(Metadata metadata,
                          BootstrapContext bootstrapContext,
                          SessionFactoryImplementor sessionFactory) {
        SessionFactoryServiceRegistry serviceRegistry = (SessionFactoryServiceRegistry) sessionFactory.getServiceRegistry();
        EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
        if (eventListenerRegistry == null) {
            return;
        }

        eventListenerRegistry.appendListeners(EventType.FLUSH_ENTITY, DomainEventStorageImpl.class);

        eventListenerRegistry.appendListeners(EventType.POST_COMMIT_INSERT, HibernateDomainEventDispatcher.class);
        eventListenerRegistry.appendListeners(EventType.POST_COMMIT_UPDATE, HibernateDomainEventDispatcher.class);
        eventListenerRegistry.appendListeners(EventType.POST_COMMIT_DELETE, HibernateDomainEventDispatcher.class);
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

    }
}
