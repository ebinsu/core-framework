package core.framework.jpa.eclipselink;

import core.framework.ddd.AggregateRoot;
import core.framework.jpa.common.support.DomainEventStoreImpl;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

/**
 * @author ebin
 */
public class DomainEventSessionEventListener extends SessionEventAdapter {

    @Override
    public void preCommitUnitOfWork(SessionEvent event) {
        if (event.getSource() instanceof RepeatableWriteUnitOfWork unitOfWork) {
            unitOfWork.getCloneMapping().keySet().stream().filter(obj -> obj instanceof AggregateRoot<?, ?>)
                    .forEach(agg ->
                            DomainEventStoreImpl.INSTANCE.persist((AggregateRoot<?, ?>) agg)
                    );
        }
    }
}
