package core.framework.namedquery.support.node.sql;

import core.framework.namedquery.QueryType;
import core.framework.namedquery.support.node.MixedNode;
import core.framework.namedquery.support.node.Node;

import java.util.List;

/**
 * @author ebin
 */
public class SqlNode extends MixedNode {

    public SqlNode(String namespace, String id, Class<?> resultClass, List<Node> nodes) {
        super(namespace, id, QueryType.SQL, resultClass, nodes);
    }
}
