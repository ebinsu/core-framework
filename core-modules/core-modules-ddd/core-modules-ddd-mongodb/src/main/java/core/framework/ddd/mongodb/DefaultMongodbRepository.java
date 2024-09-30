package core.framework.ddd.mongodb;

import core.framework.ddd.mongodb.support.ExtendMongoTemplate;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * @author ebin
 */
public class DefaultMongodbRepository<T extends AbstractAggregateRoot> extends AbstractMongodbRepository<T> {
    private final ExtendMongoTemplate extendMongoTemplate;

    public DefaultMongodbRepository(ExtendMongoTemplate extendMongoTemplate) {
        this.extendMongoTemplate = extendMongoTemplate;
    }

    @Override
    public MongoOperations getMongoOperations() {
        return extendMongoTemplate;
    }
}
