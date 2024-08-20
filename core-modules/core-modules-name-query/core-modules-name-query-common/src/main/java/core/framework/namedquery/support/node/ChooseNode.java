package core.framework.namedquery.support.node;

import core.framework.namedquery.support.NamedQueryContext;
import core.framework.namedquery.support.parser.XMLNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
public class ChooseNode implements Node {
    private final List<IfNode> ifNodes;
    private final OtherwiseNode otherwiseNode;

    public ChooseNode(List<IfNode> ifNodes, OtherwiseNode otherwiseNode) {
        this.ifNodes = ifNodes;
        this.otherwiseNode = otherwiseNode;
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        for (IfNode sqlNode : ifNodes) {
            if (sqlNode.apply(context)) {
                return true;
            }
        }
        if (otherwiseNode != null) {
            otherwiseNode.apply(context);
            return true;
        }
        return false;
    }

    public static class Builder implements NodeBuilder {

        @Override
        public Node build(String namespace, XMLNode nodeToHandle) {
            List<IfNode> ifNodes = new ArrayList<>();
            OtherwiseNode otherwiseNode = null;
            List<XMLNode> children = nodeToHandle.getChildren();
            for (XMLNode child : children) {
                String nodeName = child.getName();
                NodeBuilder nodeBuilder = nodeToHandle.getResolverContext().getNodeBuilder(nodeName);
                if (nodeBuilder instanceof IfNode.Builder) {
                    ifNodes.add((IfNode) nodeBuilder.build(namespace, child));
                } else if (nodeBuilder instanceof OtherwiseNode.Builder) {
                    otherwiseNode = (OtherwiseNode) nodeBuilder.build(namespace, child);
                }
            }
            return new ChooseNode(ifNodes, otherwiseNode);
        }
    }
}
