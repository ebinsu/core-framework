package core.framework.namequery.support.node;

import core.framework.namequery.support.parser.XMLNode;

/**
 * @author ebin
 */
public interface NodeBuilder {
    Node build(XMLNode nodeToHandle);
}
