package core.framework.ddd;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public sealed interface DomainEvent permits AbstractDomainEvent {
    AggregateRootMetadata getAggregateRootMetadata();

    ZonedDateTime getCreatedTime();
}

