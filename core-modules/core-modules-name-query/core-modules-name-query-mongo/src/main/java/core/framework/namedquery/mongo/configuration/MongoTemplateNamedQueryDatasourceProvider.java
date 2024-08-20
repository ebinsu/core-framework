package core.framework.namedquery.mongo.configuration;

import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author ebin
 */
@FunctionalInterface
public interface MongoTemplateNamedQueryDatasourceProvider {
    MongoTemplate get();
}
