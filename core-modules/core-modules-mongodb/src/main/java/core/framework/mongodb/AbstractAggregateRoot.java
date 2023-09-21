package core.framework.mongodb;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author ebin
 */
public abstract class AbstractAggregateRoot<A extends AggregateRoot<A, ID>, ID> implements AggregateRoot<A, ID> {
    @Transient
    private final transient List<DomainEvent<A, ID>> domainEvents = new ArrayList<>();

    @NotNull
    @Field(name = "created_time")
    private ZonedDateTime createdTime;

    @NotNull
    @Field(name = "created_by")
    private String createdBy;

    @NotNull
    @Field(name = "updated_time")
    private ZonedDateTime updatedTime;

    @NotNull
    @Field(name = "updated_by")
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
    public DomainEvent<A, ID> registerEvent(DomainEvent<A, ID> event) {
        if (Objects.nonNull(event)) {
            this.domainEvents.add(event);
            return event;
        }
        return null;
    }

    @Override
    public List<DomainEvent<A, ID>> getDomainEvents() {
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
