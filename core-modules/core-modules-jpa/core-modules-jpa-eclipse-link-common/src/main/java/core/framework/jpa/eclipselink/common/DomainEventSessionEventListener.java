package core.framework.jpa.eclipselink.common;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.ddd.annotation.Trigger;
import core.framework.ddd.support.DomainEventBusHolder;
import core.framework.jpa.common.support.DomainEventStoreImpl;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

import java.util.List;

/**
 * @author ebin
 */
public class DomainEventSessionEventListener extends SessionEventAdapter {

    @Override
    public void preCommitUnitOfWork(SessionEvent event) {
        if (event.getSource() instanceof RepeatableWriteUnitOfWork unitOfWork) {
            List<AggregateRoot<?, ?>> aggregateRoots = unitOfWork.getCloneMapping().keySet().stream().filter(obj -> obj instanceof AggregateRoot<?, ?>).toList();
            aggregateRoots.forEach(DomainEventStoreImpl.INSTANCE::persist);

            for (AggregateRoot<?, ?> aggregateRoot : aggregateRoots) {
                List<? extends DomainEvent<?, ?>> domainEvents = aggregateRoot.getDomainEvents();
                for (DomainEvent<?, ?> domainEvent : domainEvents) {
                    DomainEventBusHolder.get().dispatch(domainEvent, Trigger.BEFORE_COMMIT);
                }
            }
        }
    }

    @Override
    public void postCommitUnitOfWork(SessionEvent event) {
        if (event.getSource() instanceof RepeatableWriteUnitOfWork unitOfWork) {
            List<AggregateRoot<?, ?>> aggregateRoots = unitOfWork.getCloneMapping().keySet().stream().filter(obj -> obj instanceof AggregateRoot<?, ?>).toList();
            for (AggregateRoot<?, ?> aggregateRoot : aggregateRoots) {
                List<? extends DomainEvent<?, ?>> domainEvents = aggregateRoot.getDomainEvents();
                for (DomainEvent<?, ?> domainEvent : domainEvents) {
                    DomainEventBusHolder.get().dispatch(domainEvent, Trigger.AFTER_COMMIT);
                }
            }
        }
    }
}
