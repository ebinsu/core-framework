package core.framework.namedquery.support.node;

import core.framework.namedquery.support.NamedQueryContext;

import java.util.List;

/**
 * @author ebin
 */
public class MixedNode implements Node {
    public static final String ID_SEPARATOR = ".";
    private final String namespace;
    private final String id;
    private final List<Node> nodes;
    private final Class<?> resultClass;
    private final String xmlTagName;

    public MixedNode(String xmlTag, String namespace, String id, Class<?> resultClass, List<Node> nodes) {
        this.xmlTagName = xmlTag;
        this.namespace = namespace;
        this.id = namespace + ID_SEPARATOR + id;
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

    public List<Node> getNodes() {
        return nodes;
    }

    public String getXmlTagName() {
        return xmlTagName;
    }
}
