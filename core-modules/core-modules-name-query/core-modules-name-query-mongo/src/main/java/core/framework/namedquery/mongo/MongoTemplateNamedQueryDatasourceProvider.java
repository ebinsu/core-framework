package core.framework.namedquery.mongo;

import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author ebin
 */
@FunctionalInterface
public interface MongoTemplateNamedQueryDatasourceProvider {
    MongoTemplate get();
}
