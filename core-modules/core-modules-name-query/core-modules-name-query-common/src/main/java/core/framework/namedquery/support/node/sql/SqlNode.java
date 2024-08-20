package core.framework.namedquery.support.node.sql;

import core.framework.namedquery.support.node.MixedNode;
import core.framework.namedquery.support.node.Node;
import core.framework.namedquery.support.node.NodeBuilder;
import core.framework.namedquery.support.parser.ChildrenNodeHelper;
import core.framework.namedquery.support.parser.XMLNode;

import java.util.List;

/**
 * @author ebin
 */
public class SqlNode extends MixedNode {

    public SqlNode(String xmlTag, String namespace, String id, Class<?> resultClass, List<Node> nodes) {
        super(xmlTag, namespace, id, resultClass, nodes);
    }

    public static class Builder implements NodeBuilder {

        @Override
        public Node build(String namespace, XMLNode nodeToHandle) {
            String id = nodeToHandle.getAttributes().getProperty("id");
            Class<?> resultClass;
            try {
                resultClass = Class.forName(nodeToHandle.getAttributes().getProperty("result-class"));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return new SqlNode(nodeToHandle.getName(), namespace, id, resultClass, ChildrenNodeHelper.build(namespace, nodeToHandle));
        }
    }
}
