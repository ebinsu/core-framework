package core.framework.ddd;

import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public interface Repository<T extends AggregateRoot<T, ID>, ID> {
    void persist(T entity);

    T merge(T entity);

    void remove(T entity);

    Optional<T> find(ID id);

    List<T> selectByQueryString(String queryString, Object... params);

    Optional<T> findByQueryString(String queryString, Object... params);

    <R> R aggregateByQueryString(String queryString, Class<R> resultClass, Object... params);
}
