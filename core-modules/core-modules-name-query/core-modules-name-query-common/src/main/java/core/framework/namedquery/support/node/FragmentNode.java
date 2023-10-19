package core.framework.namedquery.support.node;

import core.framework.namedquery.support.NamedQueryContext;

import java.util.List;

/**
 * @author ebin
 */
public class FragmentNode implements Node {
    public static final String ID_SEPARATOR = ".";
    private final String namespace;
    private final String id;
    private final List<Node> childrenNodes;

    public FragmentNode(String namespace, String id, List<Node> childrenNodes) {
        this.namespace = namespace;
        this.id = namespace + ID_SEPARATOR + id;
        this.childrenNodes = childrenNodes;
    }

    public String getId() {
        return id;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        NamedQueryContext childrenContext = new NamedQueryContext(context.getNamespace(), context.getParameter(), context.getFragmentNodes());
        childrenNodes.forEach(node -> node.apply(childrenContext));
        context.appendQuery(childrenContext.getQuery());
        childrenContext.getParameter().forEach(context::addParameter);
        return true;
    }
}
