package core.framework.namedquery;

import core.framework.namedquery.support.NamedQueryContext;
import core.framework.namedquery.support.node.MixedNode;

/**
 * @author ebin
 */
public interface NamedQueryBuilder {
    boolean support(MixedNode mixedNode);

    NamedQuery build(String name, NamedQueryContext context, MixedNode mixedNode);
}
