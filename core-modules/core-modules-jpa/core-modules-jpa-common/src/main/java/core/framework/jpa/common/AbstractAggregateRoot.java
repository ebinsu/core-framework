package core.framework.jpa.common;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author ebin
 */
@MappedSuperclass
public abstract class AbstractAggregateRoot extends AbstractEntity implements AggregateRoot {
    @Transient
    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    protected AbstractAggregateRoot() {
        this(null);
    }

    protected AbstractAggregateRoot(String createdBy) {
        super(createdBy);
    }

    @Override
    public void registerEvent(DomainEvent event) {
        if (Objects.nonNull(event)) {
            this.domainEvents.add(event);
        }
    }

    @Override
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    @Override
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
