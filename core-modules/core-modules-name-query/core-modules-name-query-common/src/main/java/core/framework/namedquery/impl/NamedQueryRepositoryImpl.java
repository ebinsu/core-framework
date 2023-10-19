package core.framework.namedquery.impl;

import core.framework.namedquery.NamedQuery;
import core.framework.namedquery.NamedQueryRepository;
import core.framework.namedquery.support.NamedQueryContext;
import core.framework.namedquery.support.node.FragmentNode;
import core.framework.namedquery.support.node.MixedNode;
import core.framework.namedquery.support.node.mongo.MongoNode;
import core.framework.namedquery.support.node.sql.SqlNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ebin
 */
public class NamedQueryRepositoryImpl implements NamedQueryRepository {

    private final Map<String, MixedNode> nodes = new ConcurrentHashMap<>();
    private final Map<String, FragmentNode> fragmentNodes = new ConcurrentHashMap<>();

    @Override
    public NamedQuery get(String queryName, Object parameter) {
        MixedNode mixedNode = nodes.get(queryName);
        NamedQueryContext context = new NamedQueryContext(mixedNode.getNamespace(), parameter, fragmentNodes);
        mixedNode.apply(context);
        if (mixedNode instanceof SqlNode) {
            return new SqlNamedQueryImpl(queryName, context.getQuery(), context.getParameter(), mixedNode.getResultClass());
        } else if (mixedNode instanceof MongoNode mongoNode) {
            return new MongoNamedQueryImpl(queryName, context.getQuery(), context.getParameter(), mixedNode.getResultClass(), mongoNode.getReadPreference().value);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void register(MixedNode node) {
        if (node != null) {
            nodes.put(node.getId(), node);
        }
    }

    @Override
    public void register(FragmentNode node) {
        if (node != null) {
            fragmentNodes.put(node.getId(), node);
        }
    }
}
