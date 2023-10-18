package core.framework.namedquery.support.node;

import core.framework.namedquery.support.NamedQueryContext;

/**
 * @author ebin
 */
public interface Node {
    boolean apply(NamedQueryContext context);
}
