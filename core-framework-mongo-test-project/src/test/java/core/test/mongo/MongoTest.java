package core.test.mongo;

import core.framework.mongodb.support.ExtendMongoTemplate;
import core.test.mongodb.CreatedMongoDomainEvent;
import core.test.mongodb.MongoDomain;
import core.test.mongodb.MongoDomainRepo;
import org.bson.Document;
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

    @Autowired
    ExtendMongoTemplate extendMongoTemplate;

    @Test
    public void testInsert() {
        MongoDomain mongoDomain = new MongoDomain();
        mongoDomain.registerEvent(new CreatedMongoDomainEvent());
        mongoDomainRepo.persist(mongoDomain);
        System.out.println(mongoDomain.getId());
    }

    @Test
    public void testQuery() {
        Document document = extendMongoTemplate.executeCommand("""
            {
              find: "domain_event_tracking",
              filter: {
                  '_id' : new ObjectId("650c00e9db7e514eb749b086")
              }
            }
            """);
        System.out.println(document);
    }
}
