package core.framework.query.support.namequery;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public class NameNameQueryParam<T> implements core.framework.query.support.NameQueryParam<T> {
    private final Map<String, Object> queryParams = new HashMap<>();
    private final String queryName;
    private final Class<T> resultType;

    public NameNameQueryParam(String queryName, Class<T> resultType) {
        this.queryName = queryName;
        this.resultType = resultType;
    }

    @Override
    public String getQueryName() {
        return queryName;
    }

    @Override
    public Class<T> getResultType() {
        return resultType;
    }

    public Map<String, Object> getQueryParam() {
        return Collections.unmodifiableMap(queryParams);
    }

    public NameNameQueryParam<T> addQueryParam(String key, Object value) {
        queryParams.put(key, value);
        return this;
    }
}
