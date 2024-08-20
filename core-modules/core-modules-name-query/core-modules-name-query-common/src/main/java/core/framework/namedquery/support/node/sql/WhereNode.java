package core.framework.namedquery.support.node.sql;

import core.framework.namedquery.support.node.Node;
import core.framework.namedquery.support.node.NodeBuilder;
import core.framework.namedquery.support.node.TrimNode;
import core.framework.namedquery.support.parser.ChildrenNodeHelper;
import core.framework.namedquery.support.parser.XMLNode;

import java.util.Arrays;
import java.util.List;

/**
 * @author ebin
 */
public class WhereNode extends TrimNode {
    private static final List<String> PREFIX_LIST = Arrays.asList("AND ", "OR ", "AND\n", "OR\n", "AND\r", "OR\r", "AND\t", "OR\t");

    public WhereNode(List<Node> childrenNodes) {
        super(childrenNodes, "WHERE", PREFIX_LIST, null, null, false);
    }

    public static class Builder implements NodeBuilder {

        @Override
        public Node build(String namespace, XMLNode nodeToHandle) {
            List<Node> childrenNodes = ChildrenNodeHelper.build(namespace, nodeToHandle);
            return new WhereNode(childrenNodes);
        }
    }
}
