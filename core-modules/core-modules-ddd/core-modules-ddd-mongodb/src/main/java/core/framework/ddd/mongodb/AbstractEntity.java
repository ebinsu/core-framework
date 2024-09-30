package core.framework.ddd.mongodb;

import core.framework.ddd.api.Entity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public abstract class AbstractEntity implements Entity {
    @NotNull
    @Field(name = "created_time")
    private ZonedDateTime createdTime;

    @Field(name = "created_by")
    private String createdBy;

    @NotNull
    @Field(name = "updated_time")
    private ZonedDateTime updatedTime;

    @Field(name = "updated_by")
    private String updatedBy;

    protected AbstractEntity() {
        this(null);
    }

    protected AbstractEntity(String createdBy) {
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

    protected final void setCreatedInfo(String createdBy) {
        this.createdBy = createdBy;
        this.createdTime = ZonedDateTime.now();
    }

    protected final void setUpdatedInfo(String updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedTime = ZonedDateTime.now();
    }
}
