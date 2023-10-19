package core.framework.namedquery.support;

import core.framework.json.JSONMapper;
import core.framework.namedquery.support.node.FragmentNode;
import core.framework.namedquery.support.parser.QueryStringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * @author ebin
 */
public class NamedQueryContext {
    private final String namespace;
    private final Map<String, FragmentNode> fragmentNodes;
    private final Map<String, Object> parameter;

    private final StringJoiner queryBuilder = new StringJoiner(" ");

    public NamedQueryContext(String namespace, Object parameter, Map<String, FragmentNode> fragmentNodes) {
        if (parameter != null) {
            if (parameter instanceof Map<?, ?> map) {
                this.parameter = (Map<String, Object>) new HashMap<>(map);
            } else {
                this.parameter = (Map<String, Object>) JSONMapper.OBJECT_MAPPER.convertValue(parameter, Map.class);
            }
        } else {
            this.parameter = new HashMap<>(0);
        }
        this.fragmentNodes = fragmentNodes;
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }

    public Map<String, FragmentNode> getFragmentNodes() {
        return Collections.unmodifiableMap(fragmentNodes);
    }

    public Optional<FragmentNode> getFragmentNode(String id) {
        return Optional.ofNullable(fragmentNodes.get(id));
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
