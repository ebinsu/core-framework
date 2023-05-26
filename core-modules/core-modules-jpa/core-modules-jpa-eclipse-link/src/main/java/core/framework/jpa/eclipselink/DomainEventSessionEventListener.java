package core.framework.jpa.eclipselink;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author ebin
 */
public class DomainEventSessionEventListener extends SessionEventAdapter {
    @Override
    public void preCommitUnitOfWork(SessionEvent event) {
        if (event.getSource() instanceof RepeatableWriteUnitOfWork unitOfWork) {
            List<DomainEvent<?, ?>> domainEvents = unitOfWork.getCloneMapping().keySet().stream()
                .flatMap(obj -> {
                    if (obj instanceof AggregateRoot<?, ?> agg) {
                        return agg.getDomainEvents().stream();
                    } else {
                        return Stream.empty();
                    }
                }).toList();


        }
    }
}
