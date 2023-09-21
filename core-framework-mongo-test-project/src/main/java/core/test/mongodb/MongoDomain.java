package core.test.mongodb;

import core.framework.mongodb.AbstractAggregateRoot;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * @author ebin
 */
public class MongoDomain extends AbstractAggregateRoot<MongoDomain, ObjectId> {
    @MongoId
    public ObjectId id;

    @Override
    public ObjectId getId() {
        return id;
    }
}
