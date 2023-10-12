package core.framework.namequery.support.node;

import core.framework.namequery.QueryType;

import java.util.List;

/**
 * @author ebin
 */
public class SqlNode extends MixedNode {

    public SqlNode(String id, QueryType queryType, Class<?> resultClass, List<Node> nodes) {
        super(id, queryType, resultClass, nodes);
    }
}
