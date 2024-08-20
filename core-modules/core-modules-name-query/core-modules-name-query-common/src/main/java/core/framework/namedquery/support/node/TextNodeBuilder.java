package core.framework.namedquery.support.node;

import core.framework.namedquery.support.parser.XMLNode;

/**
 * @author ebin
 */
public class TextNodeBuilder implements NodeBuilder {

    @Override
    public Node build(String namespace, XMLNode nodeToHandle) {
        String data = nodeToHandle.getBody();
        TextNode textSqlNode = new TextNode(data);
        if (textSqlNode.isDynamic()) {
            return textSqlNode;
        } else {
            return new StaticTextNode(data);
        }
    }
}
