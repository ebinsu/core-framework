package core.framework.ddd.api;

import java.io.Serializable;

/**
 * @author ebin
 */
public class AggregateRootMetadata implements Serializable {
    private Serializable id;
    private String type;

    private AggregateRootMetadata() {
    }

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
