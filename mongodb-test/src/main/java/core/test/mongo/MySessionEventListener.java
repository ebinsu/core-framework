package core.test.mongo;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

/**
 * @author ebin
 */
public class MySessionEventListener extends SessionEventAdapter {
    @Override
    public void preCommitUnitOfWork(SessionEvent event) {
//        ((RepeatableWriteUnitOfWork)event.getSource()).getCloneMapping().keySet()
        DomainEventTracking domainEventTracking = new DomainEventTracking();
        EntityManagerHolder.get().persist(domainEventTracking);
    }
}
