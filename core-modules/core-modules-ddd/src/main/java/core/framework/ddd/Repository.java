package core.framework.ddd;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public interface Repository<T extends AggregateRoot> {
    void persist(T entity);

    T merge(T entity);

    void remove(T entity);

    Optional<T> find(Serializable id);

    Optional<T> find(String queryString, Object... params);

    List<T> select(String queryString, Object... params);

    <R> R aggregateByQueryString(String queryString, Class<R> resultClass, Object... params);
}
