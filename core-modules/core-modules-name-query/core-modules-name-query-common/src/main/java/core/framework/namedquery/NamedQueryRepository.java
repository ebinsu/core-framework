package core.framework.namedquery;

import core.framework.namedquery.support.node.FragmentNode;
import core.framework.namedquery.support.node.MixedNode;

import java.util.Map;

/**
 * @author ebin
 */
public interface NamedQueryRepository {
    NamedQuery get(String queryName, Map<String, Object> parameter);

    void register(MixedNode node);

    void register(FragmentNode node);
}
