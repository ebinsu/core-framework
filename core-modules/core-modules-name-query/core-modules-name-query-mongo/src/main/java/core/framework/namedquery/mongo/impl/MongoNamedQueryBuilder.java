package core.framework.namedquery.mongo.impl;

import core.framework.namedquery.NamedQuery;
import core.framework.namedquery.NamedQueryBuilder;
import core.framework.namedquery.mongo.impl.node.MongoNode;
import core.framework.namedquery.support.NamedQueryContext;
import core.framework.namedquery.support.node.MixedNode;

/**
 * @author ebin
 */
public class MongoNamedQueryBuilder implements NamedQueryBuilder {
    @Override
    public boolean support(MixedNode mixedNode) {
        return mixedNode instanceof MongoNode;
    }

    @Override
    public NamedQuery build(String queryName, NamedQueryContext context, MixedNode mixedNode) {
        if (mixedNode instanceof MongoNode mongoNode) {
            return new MongoNamedQueryImpl(mixedNode.getXmlTagName(), queryName, context.getQuery(), context.getParameter(), mixedNode.getResultClass(), mongoNode.getReadPreference().value);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
