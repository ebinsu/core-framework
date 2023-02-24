package core.framework.ddd;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public interface Entity<T extends AggregateRoot<T, ?>, ID> extends Identifiable<ID> {
    ZonedDateTime getCreatedTime();

    ZonedDateTime getUpdatedTime();
}
