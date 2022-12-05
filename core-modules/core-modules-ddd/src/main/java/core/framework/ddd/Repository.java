package core.framework.ddd;

import java.util.List;

/**
 * @author ebin
 */
public interface Repository<T extends AggregateRoot<T>> {
    int START_INDEX = 0;
    int HINT_FETCH_SIZE = 1;

    void persist(T entity);

    T merge(T entity);

    void remove(T entity);

    T find(Object id);

    List<T> selectByQueryString(String queryString, Object... params);

    T findByQueryString(String queryString, Object... params);
}
