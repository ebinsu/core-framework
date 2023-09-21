package core.test.mongodb;

import core.framework.mongodb.DefaultMongodbRepository;
import core.framework.mongodb.support.ExtendMongoTemplate;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

/**
 * @author ebin
 */
@Repository
public class MongoDomainRepo extends DefaultMongodbRepository<MongoDomain, ObjectId> {

    public MongoDomainRepo(ExtendMongoTemplate extendMongoTemplate) {
        super(extendMongoTemplate);
    }
}
