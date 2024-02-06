package core.framework.mongodb;

import core.framework.ddd.Repository;
import org.springframework.data.mongodb.core.MongoOperations;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author ebin
 */
public abstract class AbstractMongodbRepository<T extends AbstractAggregateRoot<T, ID>, ID> implements Repository<T, ID> {
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
    public Optional<T> find(ID id) {
        return Optional.ofNullable(getMongoOperations().findOne(query(where("_id").is(id)), entityClass));
    }

    protected abstract MongoOperations getMongoOperations();
}
