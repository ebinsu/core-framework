package core.framework.ddd.mongodb.support;

import core.framework.ddd.api.AggregateRoot;
import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;

/**
 * @author ebin
 */
public class AggregateRootAfterDeleteEvent extends MongoMappingEvent<AggregateRoot> {

    /**
     * Creates new {@link MongoMappingEvent}.
     *
     * @param source         must not be {@literal null}.
     * @param document       can be {@literal null}.
     * @param collectionName can be {@literal null}.
     */
    public AggregateRootAfterDeleteEvent(AggregateRoot source, Document document, String collectionName) {
        super(source, document, collectionName);
    }
}
