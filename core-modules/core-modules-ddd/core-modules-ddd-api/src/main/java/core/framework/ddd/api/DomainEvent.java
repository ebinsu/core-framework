package core.framework.ddd.api;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public sealed interface DomainEvent permits AbstractDomainEvent {
    AggregateRootMetadata getAggregateRootMetadata();

    ZonedDateTime getCreatedTime();
}

