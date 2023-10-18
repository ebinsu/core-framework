package core.framework.namedquery.support.node;

import core.framework.namedquery.support.parser.XMLNode;

/**
 * @author ebin
 */
public interface NodeBuilder {
    Node build(XMLNode nodeToHandle);
}
