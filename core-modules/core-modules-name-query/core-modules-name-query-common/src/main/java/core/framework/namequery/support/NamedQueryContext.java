package core.framework.namequery.support;

import core.framework.json.JSONMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author ebin
 */
public class NamedQueryContext {
    private Map<String, Object> parameter;

    private final StringJoiner queryBuilder = new StringJoiner(" ");
    private final Map<String, Object> queryParameter = new HashMap<>();

    public NamedQueryContext(Object parameter) {
        if (parameter instanceof Map<?, ?> map) {
            this.parameter = (Map<String, Object>) map;
        } else {
            this.parameter = (Map<String, Object>) JSONMapper.OBJECT_MAPPER.convertValue(parameter, Map.class);
        }
    }

    public Map<String, Object> getParameter() {
        return Collections.unmodifiableMap(parameter);
    }

    public void addParameter(String key, Object object) {
        queryParameter.put(key, object);
    }

    public void appendQuery(String query) {
        queryBuilder.add(query);
    }

    public String getQuery() {
        return queryBuilder.toString().trim();
    }
}
