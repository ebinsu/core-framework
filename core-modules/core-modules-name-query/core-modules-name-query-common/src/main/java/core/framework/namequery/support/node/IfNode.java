package core.framework.namequery.support.node;

import core.framework.namequery.support.NamedQueryContext;
import core.framework.namequery.support.ognl.ExpressionEvaluator;
import core.framework.namequery.support.parser.ChildrenNodeHelper;
import core.framework.namequery.support.parser.XMLNode;

import java.util.List;

/**
 * @author ebin
 */
public class IfNode implements Node {
    private final ExpressionEvaluator evaluator;
    private final String test;
    private final List<Node> childrenNodes;

    public IfNode(List<Node> childrenNodes, String test) {
        this.test = test;
        this.childrenNodes = childrenNodes;
        this.evaluator = new ExpressionEvaluator();
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        if (evaluator.evaluateBoolean(test, context.getParameter())) {
            childrenNodes.forEach(node -> node.apply(context));
            return true;
        }
        return false;
    }

    public static class Builder implements NodeBuilder {

        @Override
        public Node build(XMLNode nodeToHandle) {
            List<Node> childrenNodes = ChildrenNodeHelper.build(nodeToHandle);
            String test = nodeToHandle.getAttributes().getProperty("test");
            return new IfNode(childrenNodes, test);
        }
    }
}
