package core.framework.namedquery;

import core.framework.namedquery.support.node.MixedNode;

/**
 * @author ebin
 */
public interface NamedQueryRepository {
    NamedQuery get(String queryName, Object parameter);

    void register(MixedNode node);
}
