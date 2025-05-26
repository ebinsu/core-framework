package core.framework.ddd.api;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public interface DomainEvent {
    AggregateRootMetadata getAggregateRootMetadata();

    ZonedDateTime getCreatedTime();

    String description();
}

