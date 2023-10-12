package core.framework.namequery.support.parser;

import core.framework.namequery.support.node.Node;
import core.framework.namequery.support.node.NodeBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import static org.w3c.dom.Node.CDATA_SECTION_NODE;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

/**
 * @author ebin
 */
public class ChildrenNodeHelper {
    public static List<Node> build(XMLNode node) {
        List<Node> nodes = new ArrayList<>();
        NodeList children = node.getNode().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            XMLNode child = new XMLNode(node.getResolverContext(), children.item(i));
            if (child.getNode().getNodeType() == CDATA_SECTION_NODE || child.getNode().getNodeType() == TEXT_NODE) {
                NodeBuilder builder = node.getResolverContext().getNodeBuilder("text");
                nodes.add(builder.build(child));
            } else if (child.getNode().getNodeType() == ELEMENT_NODE) {
                String nodeName = child.getNode().getNodeName();
                NodeBuilder nodeBuilder = node.getResolverContext().getNodeBuilder(nodeName);
                if (nodeBuilder == null) {
                    throw new BuilderException("Unknown element <" + nodeName + "> in SQL statement.");
                }
                nodes.add(nodeBuilder.build(child));
            }
        }
        return nodes;
    }
}
