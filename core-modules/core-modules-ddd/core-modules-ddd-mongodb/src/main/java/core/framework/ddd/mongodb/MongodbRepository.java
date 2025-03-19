package core.framework.ddd.mongodb;

import core.framework.ddd.api.Repository;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author ebin
 */
public interface MongodbRepository<T extends AbstractAggregateRoot> extends Repository<T, Query> {
}
