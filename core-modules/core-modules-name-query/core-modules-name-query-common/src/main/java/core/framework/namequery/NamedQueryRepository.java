package core.framework.namequery;

import core.framework.namequery.support.node.MixedNode;

/**
 * @author ebin
 */
public interface NamedQueryRepository {
    NamedQuery get(String queryName, Object parameter);

    void register(MixedNode node);
}
