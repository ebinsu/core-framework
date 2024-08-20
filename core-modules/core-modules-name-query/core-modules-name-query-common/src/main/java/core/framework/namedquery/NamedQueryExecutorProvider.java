package core.framework.namedquery;


import java.util.Map;

/**
 * @author ebin
 */
@FunctionalInterface
public interface NamedQueryExecutorProvider {
    Map<String, NamedQueryExecutor> get();
}
