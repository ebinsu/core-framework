package core.framework.namedquery.impl;

import core.framework.namedquery.NamedQueryRepository;
import core.framework.namedquery.support.node.FragmentNode;
import core.framework.namedquery.support.node.MixedNode;
import core.framework.namedquery.support.node.NodeBuilderProvider;
import core.framework.namedquery.support.parser.NamedQueryXMLParser;
import core.framework.shared.utils.ResourcePatternResolverUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author ebin
 */
public class NamedQueryRepositoryInitialize implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        NamedQueryRepository repository = applicationContext.getBean(NamedQueryRepository.class);
        Collection<NodeBuilderProvider> nodeBuilderProviders = applicationContext.getBeansOfType(NodeBuilderProvider.class).values();
        List<Resource> resources;
        try {
            resources = ResourcePatternResolverUtil.resolve("**/*Query.xml");
        } catch (IOException e) {
            throw new Error(e);
        }
        NamedQueryXMLParser namedQueryXMLParser = new NamedQueryXMLParser();
        Pair<List<MixedNode>, List<FragmentNode>> parse = namedQueryXMLParser.parse(nodeBuilderProviders, resources);
        parse.getLeft().forEach(repository::register);
        parse.getRight().forEach(repository::register);
    }
}
