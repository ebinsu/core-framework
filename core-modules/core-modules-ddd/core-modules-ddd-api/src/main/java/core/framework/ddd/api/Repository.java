package core.framework.ddd.api;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public interface Repository<T extends AggregateRoot, Q> {
    void persist(T entity);

    T merge(T entity);

    void remove(T entity);

    Optional<T> find(Serializable id);

    Optional<T> find(Q query);

    List<T> select(Q query);
}
