package core.framework.namequery.support.node;

import core.framework.namequery.support.parser.XMLNode;

/**
 * @author ebin
 */
public class TextNodeBuilder implements NodeBuilder {

    @Override
    public Node build(XMLNode nodeToHandle) {
        String data = nodeToHandle.getBody();
        TextNode textSqlNode = new TextNode(data);
        if (textSqlNode.isDynamic()) {
            return textSqlNode;
        } else {
            return new StaticTextNode(data);
        }
    }
}
