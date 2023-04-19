package core.framework.query.support.namequery;

import java.util.Map;

/**
 * @author ebin
 */
public interface NameQueryRepository {
    QueryStatement getQueryStatement(String queryName, Map<String, Object> param);
}
