package core.framework.ddd.api;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public interface Entity {
    Serializable getId();

    ZonedDateTime getCreatedTime();

    ZonedDateTime getUpdatedTime();
}
