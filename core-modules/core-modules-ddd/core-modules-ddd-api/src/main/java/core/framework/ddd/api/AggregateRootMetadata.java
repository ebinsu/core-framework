package core.framework.ddd.api;

import java.io.Serializable;

/**
 * @author ebin
 */
public class AggregateRootMetadata {
    private final Serializable id;
    private final String type;

    public AggregateRootMetadata(AggregateRoot aggregateRoot) {
        this.id = aggregateRoot.getId();
        this.type = aggregateRoot.getClass().getName();
    }

    public Serializable getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }
}
