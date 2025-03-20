package core.framework.namedquery.mongo;

import core.framework.namedquery.NamedQueryService;
import core.framework.namedquery.configuration.NamedQueryConfiguration;
import core.framework.namedquery.mongo.configuration.MongoTemplateQueryServiceConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;
import java.util.Optional;

/**
 * @author ebin
 */
@Disabled
@SpringBootTest(classes = {
    TestConfig.class,
    NamedQueryConfiguration.class,
    MongoTemplateQueryServiceConfiguration.class,
    MongoAutoConfiguration.class,
    MongoDataAutoConfiguration.class
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
