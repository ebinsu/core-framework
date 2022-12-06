package core.framework.query;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public class QueryCommand<T> {
    private final Map<String, Object> queryParams = new HashMap<>();
    private final String queryName;
    private final Class<T> resultType;

    public QueryCommand(String queryName, Class<T> resultType) {
        this.queryName = queryName;
        this.resultType = resultType;
    }

    public String getQueryName() {
        return queryName;
    }

    public Class<T> getResultType() {
        return resultType;
    }

    public Map<String, Object> getQueryParam() {
        return Collections.unmodifiableMap(queryParams);
    }

    public QueryCommand<T> addQueryParam(String key, Object value) {
        queryParams.put(key, value);
        return this;
    }
}
