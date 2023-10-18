package core.framework.namedquery.support;

import core.framework.json.JSONMapper;
import core.framework.namedquery.support.parser.QueryStringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author ebin
 */
public class NamedQueryContext {
    private Map<String, Object> parameter;

    private final StringJoiner queryBuilder = new StringJoiner(" ");

    public NamedQueryContext(Object parameter) {
        if (parameter != null) {
            if (parameter instanceof Map<?, ?> map) {
                this.parameter = (Map<String, Object>) new HashMap<>(map);
            } else {
                this.parameter = (Map<String, Object>) JSONMapper.OBJECT_MAPPER.convertValue(parameter, Map.class);
            }
        } else {
            this.parameter = new HashMap<>(0);
        }
    }

    public Map<String, Object> getParameter() {
        return parameter;
    }

    public void addParameter(String key, Object object) {
        parameter.put(key, object);
    }

    public void appendQuery(String query) {
        queryBuilder.add(query);
    }

    public String getQuery() {
        return QueryStringUtils.removeExtraWhitespaces(queryBuilder.toString().trim());
    }
}
