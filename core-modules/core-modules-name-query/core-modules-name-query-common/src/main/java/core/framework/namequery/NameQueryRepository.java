package core.framework.namequery;

import java.util.Map;

/**
 * @author ebin
 */
public interface NameQueryRepository {
    QueryStatement getQueryStatement(String queryName, Map<String, Object> param);
}
