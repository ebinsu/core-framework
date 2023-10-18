package core.framework.namedquery.support.node.mongo;

import core.framework.namedquery.QueryType;
import core.framework.namedquery.support.node.MixedNode;
import core.framework.namedquery.support.node.Node;

import java.util.List;

/**
 * @author ebin
 */
public class MongoNode extends MixedNode {
    private final ReadPreference readPreference;

    public MongoNode(String id, Class<?> resultClass, List<Node> nodes, ReadPreference readPreference) {
        super(id, QueryType.MONGODB, resultClass, nodes);
        this.readPreference = readPreference;
    }

    public ReadPreference getReadPreference() {
        return readPreference;
    }

    public enum ReadPreference {
        PRIMARY("primary"),
        SECONDARY("secondary"),
        SECONDARY_PREFERRED("secondaryPreferred"),
        PRIMARY_PREFERRED("primaryPreferred"),
        NEAREST("nearest");

        public final String value;

        ReadPreference(String value) {
            this.value = value;
        }
    }
}
