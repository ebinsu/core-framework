package core.framework.namedquery.mongo.impl.node;

import core.framework.namedquery.support.node.NodeBuilderProvider;
import core.framework.namedquery.support.node.NodeBuilder;

import java.util.Map;

/**
 * @author ebin
 */
public class MongoNodeBuilderProvider implements NodeBuilderProvider {
    @Override
    public Map<String, NodeBuilder> get() {
        return Map.of(
            "mongo", new MongoNode.Builder(),
            "filter", new FilterNode.Builder()
        );
    }
}
