package core.framework.namequery.support.node;

import core.framework.namequery.QueryType;

import java.util.List;

import static core.framework.namequery.support.node.MongoNode.ReadPreference.SECONDARY_PREFERRED;

/**
 * @author ebin
 */
public class MongoNode extends MixedNode {
    private ReadPreference readPreference;

    public MongoNode(String id, QueryType queryType, Class<?> resultClass, List<Node> nodes, ReadPreference readPreference) {
        super(id, queryType, resultClass, nodes);
        if (readPreference == null) {
            readPreference = SECONDARY_PREFERRED;
        } else {
            this.readPreference = readPreference;
        }
    }

    public ReadPreference getReadPreference() {
        return readPreference;
    }

    public enum ReadPreference {
        PRIMARY,
        SECONDARY,
        SECONDARY_PREFERRED,
        PRIMARY_PREFERRED,
        NEAREST
    }
}
