package core.test.mongo;

import core.test.mongodb.CreatedMongoDomainEvent;
import core.test.mongodb.MongoDomain;
import core.test.mongodb.MongoDomainRepo;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ebin
 */
@SpringBootTest
public class MongoTest {
    @Autowired
    MongoDomainRepo mongoDomainRepo;

    @Test
    public void testInsert() {
        MongoDomain mongoDomain = new MongoDomain();
        mongoDomain.registerEvent(new CreatedMongoDomainEvent(mongoDomain));
        mongoDomainRepo.persist(mongoDomain);
        System.out.println(mongoDomain.getId());
    }
}
