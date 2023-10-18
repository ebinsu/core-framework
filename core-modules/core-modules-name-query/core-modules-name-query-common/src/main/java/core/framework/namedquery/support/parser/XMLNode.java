package core.framework.namedquery.support.parser;

import core.framework.namedquery.support.ResolverContext;
import org.w3c.dom.CharacterData;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author ebin
 */
public class XMLNode {
    private final Node node;
    private final String name;
    private final String body;
    private final Properties attributes;
    private final ResolverContext resolverContext;

    public XMLNode(ResolverContext resolverContext, Node node) {
        this.node = node;
        this.name = node.getNodeName();
        this.attributes = parseAttributes(node);
        this.body = parseBody(node);
        this.resolverContext = resolverContext;
    }

    public ResolverContext getResolverContext() {
        return resolverContext;
    }

    public Node getNode() {
        return node;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body == null ? "" : body;
    }

    public Properties getAttributes() {
        return attributes;
    }

    public List<XMLNode> getChildren() {
        List<XMLNode> children = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0, n = nodeList.getLength(); i < n; i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                children.add(new XMLNode(this.resolverContext, node));
            }
        }
        return children;
    }

    private Properties parseAttributes(Node n) {
        Properties attributes = new Properties();
        NamedNodeMap attributeNodes = n.getAttributes();
        if (attributeNodes != null) {
            for (int i = 0; i < attributeNodes.getLength(); i++) {
                Node attribute = attributeNodes.item(i);
                attributes.put(attribute.getNodeName(), attribute.getNodeValue());
            }
        }
        return attributes;
    }

    private String parseBody(Node node) {
        String data = getBodyData(node);
        if (data == null) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                data = getBodyData(child);
                if (data != null) {
                    break;
                }
            }
        }
        return data;
    }

    private String getBodyData(Node child) {
        if (child.getNodeType() == Node.CDATA_SECTION_NODE || child.getNodeType() == Node.TEXT_NODE) {
            return ((CharacterData) child).getData();
        }
        return null;
    }
}
