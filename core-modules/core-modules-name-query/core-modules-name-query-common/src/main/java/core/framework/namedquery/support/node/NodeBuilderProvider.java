package core.framework.namedquery.support.node;

import java.util.Map;

/**
 * @author ebin
 */
@FunctionalInterface
public interface NodeBuilderProvider {
    Map<String, NodeBuilder> get();
}
