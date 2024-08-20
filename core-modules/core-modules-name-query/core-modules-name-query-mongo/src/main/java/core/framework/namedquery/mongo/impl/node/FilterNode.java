package core.framework.namedquery.mongo.impl.node;

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
public class FilterNode extends TrimNode {
    private static final List<String> TRIM_LIST = Arrays.asList(
        ",",
        ", ",
        ",\n",
        ",\r",
        ",\t");

    public FilterNode(List<Node> childrenNodes) {
        super(childrenNodes, "filter: {", TRIM_LIST, "}", TRIM_LIST, true);
    }

    public static class Builder implements NodeBuilder {

        @Override
        public Node build(String namespace, XMLNode nodeToHandle) {
            List<Node> childrenNodes = ChildrenNodeHelper.build(namespace, nodeToHandle);
            return new FilterNode(childrenNodes);
        }
    }
}
