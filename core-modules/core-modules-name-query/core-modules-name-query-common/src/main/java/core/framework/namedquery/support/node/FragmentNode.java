package core.framework.namedquery.support.node;

import core.framework.namedquery.support.NamedQueryContext;

import java.util.List;

/**
 * @author ebin
 */
public class FragmentNode implements Node {
    private final String id;
    private final List<Node> childrenNodes;

    public FragmentNode(String id, List<Node> childrenNodes) {
        this.id = id;
        this.childrenNodes = childrenNodes;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        NamedQueryContext childrenContext = new NamedQueryContext(context.getParameter(), context.getFragmentNodes());
        childrenNodes.forEach(node -> node.apply(childrenContext));
        context.appendQuery(childrenContext.getQuery());
        childrenContext.getParameter().forEach(context::addParameter);
        return true;
    }
}
