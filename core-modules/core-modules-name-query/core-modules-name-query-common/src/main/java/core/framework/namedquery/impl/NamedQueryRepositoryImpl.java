package core.framework.namedquery.impl;

import core.framework.namedquery.NamedQuery;
import core.framework.namedquery.NamedQueryBuilder;
import core.framework.namedquery.NamedQueryRepository;
import core.framework.namedquery.support.NamedQueryContext;
import core.framework.namedquery.support.node.FragmentNode;
import core.framework.namedquery.support.node.MixedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ebin
 */
public class NamedQueryRepositoryImpl implements NamedQueryRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(NamedQueryRepositoryImpl.class);
    private final Map<String, MixedNode> nodes = new ConcurrentHashMap<>();
    private final Map<String, FragmentNode> fragmentNodes = new ConcurrentHashMap<>();
    private final List<NamedQueryBuilder> namedQueryBuilders = new ArrayList<>();

    public NamedQueryRepositoryImpl() {
        namedQueryBuilders.add(new SqlNamedQueryBuilder());
    }

    @Override
    public NamedQuery get(String queryName, Map<String, Object> parameter) {
        MixedNode mixedNode = nodes.get(queryName);
        if (mixedNode == null) {
            throw new RuntimeException("Query [" + queryName + "] not found !");
        }
        NamedQueryContext context = new NamedQueryContext(mixedNode.getNamespace(), parameter, fragmentNodes);
        mixedNode.apply(context);
        return namedQueryBuilders.stream().filter(builder -> builder.support(mixedNode))
            .findFirst()
            .map(builder -> builder.build(queryName, context, mixedNode))
            .orElseThrow(UnsupportedOperationException::new);
    }

    @Override
    public void register(MixedNode node) {
        if (node != null) {
            if (nodes.containsKey(node.getId())) {
                throw new RuntimeException("Named query [" + node.getId() + "] already exists !");
            }
            nodes.put(node.getId(), node);
            LOGGER.info("Register mixedNode {}", node.getId());
        }
    }

    @Override
    public void register(FragmentNode node) {
        if (node != null) {
            if (fragmentNodes.containsKey(node.getId())) {
                throw new RuntimeException("Fragment [" + node.getId() + "] already exists !");
            }
            fragmentNodes.put(node.getId(), node);
            LOGGER.info("Register fragment node {}", node.getId());
        }
    }

    @Override
    public void remove(MixedNode node) {
        if (node != null) {
            nodes.remove(node.getId());
            LOGGER.info("Remove mixedNode {}", node.getId());
        }
    }

    @Override
    public void remove(FragmentNode node) {
        if (node != null) {
            fragmentNodes.remove(node.getId());
            LOGGER.info("Remove fragment node {}", node.getId());
        }
    }

    public void addNamedQueryBuilder(NamedQueryBuilder namedQueryBuilder) {
        namedQueryBuilders.add(namedQueryBuilder);
    }
}
