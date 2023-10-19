package core.framework.namedquery.spring.mongo;

import core.framework.namedquery.NamedQueryService;
import core.framework.namedquery.configuration.NamedQueryConfiguration;
import core.framework.namedquery.spring.mongo.configuration.MongoTemplateQueryServiceConfiguration;
import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ebin
 */
@SpringBootTest(classes = {
    TestConfig.class,
    NamedQueryConfiguration.class,
    MongoTemplateQueryServiceConfiguration.class,
    EmbeddedMongoAutoConfiguration.class,
    MongoAutoConfiguration.class,
    MongoDataAutoConfiguration.class
}, properties = {
    "de.flapdoodle.mongodb.embedded.version=7.0.0"
})
public class MongoTemplateQueryServiceTest {
    @Autowired
    NamedQueryService namedQueryService;
    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void test() {
        TestEntity test = new TestEntity("test");
        mongoTemplate.insert(test);
        Optional<TestDTO> get = namedQueryService.get("test.1", Map.of());
        Assertions.assertTrue(get.isPresent());
        TestDTO testDTO = get.get();
        Assertions.assertEquals(testDTO.id(), test.getId());
        Assertions.assertEquals(testDTO.name(), test.getName());
    }

}
