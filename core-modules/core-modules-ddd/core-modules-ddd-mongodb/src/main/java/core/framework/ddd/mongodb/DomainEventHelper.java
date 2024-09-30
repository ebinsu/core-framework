package core.framework.ddd.mongodb;

import core.framework.ddd.api.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author ebin
 */
class DomainEventHelper {
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public void registerEvent(DomainEvent event) {
        if (Objects.nonNull(event)) {
            this.domainEvents.add(event);
        }
    }

    protected List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    protected void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
