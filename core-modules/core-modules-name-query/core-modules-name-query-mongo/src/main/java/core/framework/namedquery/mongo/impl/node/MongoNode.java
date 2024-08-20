package core.framework.namedquery.mongo.impl.node;

import core.framework.namedquery.support.node.MixedNode;
import core.framework.namedquery.support.node.Node;
import core.framework.namedquery.support.node.NodeBuilder;
import core.framework.namedquery.support.parser.ChildrenNodeHelper;
import core.framework.namedquery.support.parser.XMLNode;

import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public class MongoNode extends MixedNode {
    private final ReadPreference readPreference;

    public MongoNode(String xmlTagName, String namespace, String id, Class<?> resultClass, List<Node> nodes, ReadPreference readPreference) {
        super(xmlTagName, namespace, id, resultClass, nodes);
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

    public static class Builder implements NodeBuilder {

        @Override
        public Node build(String namespace, XMLNode nodeToHandle) {
            String id = nodeToHandle.getAttributes().getProperty("id");
            Class<?> resultClass;
            try {
                resultClass = Class.forName(nodeToHandle.getAttributes().getProperty("result-class"));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            MongoNode.ReadPreference readPreference = Optional.ofNullable(nodeToHandle.getAttributes().getProperty("read-preference"))
                .map(MongoNode.ReadPreference::valueOf).orElse(MongoNode.ReadPreference.SECONDARY_PREFERRED);
            return new MongoNode(nodeToHandle.getName(), namespace, id, resultClass, ChildrenNodeHelper.build(namespace, nodeToHandle), readPreference);
        }
    }
}
