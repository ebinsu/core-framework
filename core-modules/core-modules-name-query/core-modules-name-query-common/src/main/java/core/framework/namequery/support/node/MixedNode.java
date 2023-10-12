package core.framework.namequery.support.node;

import core.framework.namequery.QueryType;
import core.framework.namequery.support.NamedQueryContext;

import java.util.List;
import java.util.Objects;

/**
 * @author ebin
 */
public class MixedNode implements Node {
    private final String id;
    private final QueryType queryType;
    private final List<Node> nodes;
    private final Class<?> resultClass;

    public MixedNode(String id, QueryType queryType, Class<?> resultClass, List<Node> nodes) {
        this.id = id;
        this.queryType = queryType;
        this.nodes = nodes;
        this.resultClass = resultClass;
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        nodes.forEach(node -> node.apply(context));
        return true;
    }

    public Class<?> getResultClass() {
        return resultClass;
    }

    public String id() {
        return id;
    }

    public QueryType queryType() {
        return queryType;
    }

    public List<Node> nodes() {
        return nodes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MixedNode) obj;
        return Objects.equals(this.id, that.id) &&
            Objects.equals(this.queryType, that.queryType) &&
            Objects.equals(this.nodes, that.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, queryType, nodes);
    }

    @Override
    public String toString() {
        return "MixedNode[" +
            "id=" + id + ", " +
            "queryType=" + queryType + ", " +
            "nodes=" + nodes + ']';
    }

}
