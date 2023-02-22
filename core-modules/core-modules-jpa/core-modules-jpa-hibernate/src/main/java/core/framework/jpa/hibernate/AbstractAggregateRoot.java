package core.framework.jpa.hibernate;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author ebin
 */
@MappedSuperclass
public abstract class AbstractAggregateRoot<A extends AggregateRoot<A, ID>, ID> implements AggregateRoot<A, ID> {
    @Transient
    private final transient List<DomainEvent<A>> domainEvents = new ArrayList<>();

    @NotNull
    @Column(name = "created_time")
    private ZonedDateTime createdTime = ZonedDateTime.now();

    @Override
    public ZonedDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public DomainEvent<A> registerEvent(DomainEvent<A> event) {
        if (Objects.nonNull(event)) {
            this.domainEvents.add(event);
            return event;
        }
        return null;
    }

    @Override
    public List<DomainEvent<A>> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    @Override
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
