package core.framework.jpa.hibernate.support;

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

        eventListenerRegistry.appendListeners(EventType.POST_INSERT, HibernatePreCommitEventListener.class);
        eventListenerRegistry.appendListeners(EventType.POST_UPDATE, HibernatePreCommitEventListener.class);
        eventListenerRegistry.appendListeners(EventType.POST_DELETE, HibernatePreCommitEventListener.class);

        eventListenerRegistry.appendListeners(EventType.POST_COMMIT_INSERT, HibernatePostCommitEventListener.class);
        eventListenerRegistry.appendListeners(EventType.POST_COMMIT_UPDATE, HibernatePostCommitEventListener.class);
        eventListenerRegistry.appendListeners(EventType.POST_COMMIT_DELETE, HibernatePostCommitEventListener.class);
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

    }
}
