package core.framework.namedquery;


import org.apache.commons.lang3.tuple.Pair;

/**
 * @author ebin
 */
@FunctionalInterface
public interface NamedQueryExecutorProvider {
    Pair<QueryType, NamedQueryExecutor> get();
}
