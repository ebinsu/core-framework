package core.framework.namequery;

import java.util.List;

/**
 * @author ebin
 */
public interface NamedQueryExecutor {
    <T> List<T> execute(NamedQuery namedQuery, Integer maxReturnRows);
}
