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
    private ZonedDateTime createdTime;

    @Column(name = "created_by")
    private String createdBy;

    @NotNull
    @Column(name = "updated_time")
    private ZonedDateTime updatedTime;

    @Column(name = "updated_by")
    private String updatedBy;

    protected AbstractAggregateRoot() {
        this(null);
    }

    protected AbstractAggregateRoot(String createdBy) {
        this.setCreatedInfo(createdBy);
        this.setUpdatedInfo(createdBy);
    }

    @Override
    public ZonedDateTime getCreatedTime() {
        return createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public ZonedDateTime getUpdatedTime() {
        return updatedTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
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

    protected final void setCreatedInfo(String createdBy) {
        this.createdBy = createdBy;
        this.createdTime = ZonedDateTime.now();
    }

    protected final void setUpdatedInfo(String updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedTime = ZonedDateTime.now();
    }
}
