package core.framework.namequery.support.node;

import core.framework.namequery.support.NamedQueryContext;

/**
 * @author ebin
 */
public interface Node {
    boolean apply(NamedQueryContext context);
}
