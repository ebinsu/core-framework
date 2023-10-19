package core.framework.namedquery.support.node;

import core.framework.namedquery.QueryType;
import core.framework.namedquery.support.NamedQueryContext;

import java.util.List;

/**
 * @author ebin
 */
public class MixedNode implements Node {
    public static final String ID_SEPARATOR = ".";
    private final String namespace;
    private final String id;
    private final QueryType queryType;
    private final List<Node> nodes;
    private final Class<?> resultClass;

    public MixedNode(String namespace, String id, QueryType queryType, Class<?> resultClass, List<Node> nodes) {
        this.namespace = namespace;
        this.id = namespace + ID_SEPARATOR + id;
        this.queryType = queryType;
        this.nodes = nodes;
        this.resultClass = resultClass;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        nodes.forEach(node -> node.apply(context));
        return true;
    }

    public Class<?> getResultClass() {
        return resultClass;
    }

    public String getId() {
        return id;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
