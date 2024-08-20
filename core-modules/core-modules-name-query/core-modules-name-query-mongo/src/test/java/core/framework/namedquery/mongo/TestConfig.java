package core.framework.namedquery.mongo;

import core.framework.namedquery.mongo.configuration.MongoTemplateNamedQueryDatasourceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author ebin
 */
@Configuration
public class TestConfig {
    @Bean
    public MongoTemplateNamedQueryDatasourceProvider mongoTemplateNamedQueryDatasourceProvider(MongoTemplate mongoTemplate) {
        return () -> mongoTemplate;
    }
}
