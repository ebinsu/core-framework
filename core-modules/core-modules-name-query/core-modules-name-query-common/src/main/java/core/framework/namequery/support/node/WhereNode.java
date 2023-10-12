package core.framework.namequery.support.node;

import core.framework.namequery.support.parser.ChildrenNodeHelper;
import core.framework.namequery.support.parser.XMLNode;

import java.util.Arrays;
import java.util.List;

/**
 * @author ebin
 */
public class WhereNode extends TrimNode {
    private static final List<String> prefixList = Arrays.asList("AND ", "OR ", "AND\n", "OR\n", "AND\r", "OR\r", "AND\t", "OR\t");

    public WhereNode(List<Node> childrenNodes) {
        super(childrenNodes, "WHERE", prefixList, null, null, false);
    }

    public static class Builder implements NodeBuilder {

        @Override
        public Node build(XMLNode nodeToHandle) {
            List<Node> childrenNodes = ChildrenNodeHelper.build(nodeToHandle);
            return new WhereNode(childrenNodes);
        }
    }
}
