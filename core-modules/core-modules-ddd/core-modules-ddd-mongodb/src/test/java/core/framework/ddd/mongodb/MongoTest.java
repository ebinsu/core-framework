package core.framework.ddd.mongodb;

import core.framework.ddd.common.configuration.DomainEventDispatcherConfiguration;
import core.framework.ddd.mongodb.configuration.MongodbConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author ebin
 */
@Disabled
@SpringBootTest(classes = {
    Context.class,
    DomainEventDispatcherConfiguration.class,
    MongodbConfiguration.class,
    MongoAutoConfiguration.class,
    MongoDataAutoConfiguration.class
})
public class MongoTest {
    @Autowired
    TestEntityRepo testEntityRepo;
    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void test() {
        TestEntity test = new TestEntity("test");
        CreatedEvent createdEvent = new CreatedEvent();
        test.registerEvent(createdEvent);
        testEntityRepo.persist(test);

        Assertions.assertTrue(createdEvent.handle);
        Assertions.assertNotNull(createdEvent.getAggregateRootMetadata());

        Query query = new Query();
        List<TestEntity> testEntities = mongoTemplate.find(query, TestEntity.class);
        List<DomainEventTracking> domainEventTrackings = mongoTemplate.find(query, DomainEventTracking.class);
        Assertions.assertEquals(1, testEntities.size());
        Assertions.assertEquals(1, domainEventTrackings.size());
    }
}
