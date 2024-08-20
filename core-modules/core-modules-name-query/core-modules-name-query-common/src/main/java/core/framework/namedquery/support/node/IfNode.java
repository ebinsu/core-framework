package core.framework.namedquery.support.node;

import core.framework.namedquery.support.NamedQueryContext;
import core.framework.namedquery.support.ognl.ExpressionEvaluator;
import core.framework.namedquery.support.parser.ChildrenNodeHelper;
import core.framework.namedquery.support.parser.XMLNode;

import java.util.List;

/**
 * @author ebin
 */
public class IfNode implements Node {
    private final String test;
    private final List<Node> childrenNodes;

    public IfNode(List<Node> childrenNodes, String test) {
        this.test = test;
        this.childrenNodes = childrenNodes;
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        if (ExpressionEvaluator.evaluateBoolean(test, context.getParameter())) {
            childrenNodes.forEach(node -> node.apply(context));
            return true;
        }
        return false;
    }

    public static class Builder implements NodeBuilder {

        @Override
        public Node build(String namespace, XMLNode nodeToHandle) {
            List<Node> childrenNodes = ChildrenNodeHelper.build(namespace, nodeToHandle);
            String test = nodeToHandle.getAttributes().getProperty("test");
            return new IfNode(childrenNodes, test);
        }
    }
}
