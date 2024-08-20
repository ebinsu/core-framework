package core.framework.namedquery.support.node;

import core.framework.namedquery.support.NamedQueryContext;
import core.framework.namedquery.support.parser.ChildrenNodeHelper;
import core.framework.namedquery.support.parser.XMLNode;

import java.util.List;

/**
 * @author ebin
 */
public class OtherwiseNode implements Node {
    private final List<Node> childrenNodes;

    public OtherwiseNode(List<Node> childrenNodes) {
        this.childrenNodes = childrenNodes;
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        childrenNodes.forEach(node -> node.apply(context));
        return true;
    }

    public static class Builder implements NodeBuilder {

        @Override
        public Node build(String namespace, XMLNode nodeToHandle) {
            List<Node> childrenNodes = ChildrenNodeHelper.build(namespace, nodeToHandle);
            return new OtherwiseNode(childrenNodes);
        }
    }
}
