package core.framework.namedquery.impl;

import core.framework.namedquery.NamedQuery;
import core.framework.namedquery.NamedQueryBuilder;
import core.framework.namedquery.support.NamedQueryContext;
import core.framework.namedquery.support.node.MixedNode;
import core.framework.namedquery.support.node.sql.SqlNode;

/**
 * @author ebin
 */
public class SqlNamedQueryBuilder implements NamedQueryBuilder {
    @Override
    public boolean support(MixedNode mixedNode) {
        return mixedNode instanceof SqlNode;
    }

    @Override
    public NamedQuery build(String queryName, NamedQueryContext context, MixedNode mixedNode) {
        return new SqlNamedQueryImpl(mixedNode.getXmlTagName(), queryName, context.getQuery(), context.getParameter(), mixedNode.getResultClass());
    }
}
