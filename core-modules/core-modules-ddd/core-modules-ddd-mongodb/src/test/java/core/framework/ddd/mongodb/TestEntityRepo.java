package core.framework.ddd.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * @author ebin
 */
public class TestEntityRepo extends AbstractMongodbRepository<TestEntity> {
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    protected MongoOperations getMongoOperations() {
        return mongoOperations;
    }
}