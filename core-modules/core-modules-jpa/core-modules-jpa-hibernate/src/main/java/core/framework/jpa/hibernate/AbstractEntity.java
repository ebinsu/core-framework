package core.framework.jpa.hibernate;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
@MappedSuperclass
public abstract class AbstractEntity<A extends AggregateRoot<A, ?>, ID> implements Entity<A, ID> {
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

    protected AbstractEntity() {
        this(null);
    }

    protected AbstractEntity(String createdBy) {
        this.setCreatedInfo(createdBy);
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

    protected final void setCreatedInfo(String createdBy) {
        this.createdBy = createdBy;
        this.createdTime = ZonedDateTime.now();
    }

    protected final void setUpdatedInfo(String updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedTime = ZonedDateTime.now();
    }
}
