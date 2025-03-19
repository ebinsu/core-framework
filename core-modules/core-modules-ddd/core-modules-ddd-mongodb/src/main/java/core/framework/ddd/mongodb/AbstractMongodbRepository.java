package core.framework.ddd.mongodb;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author ebin
 */
public abstract class AbstractMongodbRepository<T extends AbstractAggregateRoot> implements MongodbRepository<T> {
    private final Class<T> entityClass;

    public AbstractMongodbRepository() {
        Type actualTypeArgument = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.entityClass = (Class<T>) actualTypeArgument;
    }

    @Override
    public void persist(T entity) {
        getMongoOperations().insert(entity);
    }

    @Override
    public T merge(T entity) {
        return getMongoOperations().findAndReplace(query(where("_id").is(entity.getId())), entity);
    }

    @Override
    public void remove(T entity) {
        getMongoOperations().remove(entity);
    }

    @Override
    public Optional<T> find(Serializable id) {
        return Optional.ofNullable(getMongoOperations().findOne(query(where("_id").is(id)), entityClass));
    }

    @Override
    public Optional<T> find(Query query) {
        return Optional.ofNullable(getMongoOperations().findOne(query, this.entityClass));
    }

    @Override
    public List<T> select(Query query) {
        return getMongoOperations().find(query, this.entityClass);
    }

    protected <R> AggregationResults<R> aggregate(TypedAggregation<?> aggregation, Class<R> resultClass) {
        return getMongoOperations().aggregate(aggregation, this.entityClass, resultClass);
    }

    protected abstract MongoOperations getMongoOperations();
}
