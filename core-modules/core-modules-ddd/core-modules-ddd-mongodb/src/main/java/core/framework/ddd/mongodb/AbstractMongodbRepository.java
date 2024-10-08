package core.framework.ddd.mongodb;

import core.framework.ddd.api.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

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
public abstract class AbstractMongodbRepository<T extends AbstractAggregateRoot> implements Repository<T> {
    private final Class<T> entityClass;
    @Autowired
    private MongoOperations mongoOperations;

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
    public Optional<T> find(String queryString, Object... params) {
        return Optional.empty();
    }

    @Override
    public List<T> select(String queryString, Object... params) {
        return List.of();
    }

    @Override
    public <R> R aggregateByQueryString(String queryString, Class<R> resultClass, Object... params) {
        return null;
    }

    private MongoOperations getMongoOperations() {
        return this.mongoOperations;
    }
}
