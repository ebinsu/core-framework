package core.framework.namequery.support.node;

import core.framework.namequery.support.parser.ChildrenNodeHelper;
import core.framework.namequery.support.parser.XMLNode;

import java.util.Arrays;
import java.util.List;

/**
 * @author ebin
 */
public class FilterNode extends TrimNode {
    private static final List<String> trimList = Arrays.asList(
        ",",
        ", ",
        ",\n",
        ",\r",
        ",\t");

    public FilterNode(List<Node> childrenNodes) {
        super(childrenNodes, "filter: {", trimList, "}", trimList, true);
    }

    public static class Builder implements NodeBuilder {

        @Override
        public Node build(XMLNode nodeToHandle) {
            List<Node> childrenNodes = ChildrenNodeHelper.build(nodeToHandle);
            return new FilterNode(childrenNodes);
        }
    }
}
