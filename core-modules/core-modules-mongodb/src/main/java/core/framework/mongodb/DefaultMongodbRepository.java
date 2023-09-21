package core.framework.mongodb;

import core.framework.mongodb.support.ExtendMongoTemplate;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * @author ebin
 */
public class DefaultMongodbRepository<T extends AbstractAggregateRoot<T, ID>, ID> extends AbstractMongodbRepository<T, ID> {
    private final ExtendMongoTemplate extendMongoTemplate;

    public DefaultMongodbRepository(ExtendMongoTemplate extendMongoTemplate) {
        this.extendMongoTemplate = extendMongoTemplate;
    }

    @Override
    public MongoOperations getMongoOperations() {
        return extendMongoTemplate;
    }
}
