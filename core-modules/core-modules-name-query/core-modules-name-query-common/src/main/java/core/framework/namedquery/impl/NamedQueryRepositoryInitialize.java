package core.framework.namedquery.impl;

import core.framework.namedquery.NamedQueryRepository;
import core.framework.namedquery.support.parser.NamedQueryXMLParser;
import core.framework.shared.utils.ResourcePatternResolverUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

/**
 * @author ebin
 */
public class NamedQueryRepositoryInitialize implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        NamedQueryRepository repository = applicationContext.getBean(NamedQueryRepository.class);
        List<Resource> resources = null;
        try {
            resources = ResourcePatternResolverUtil.resolve("**/*Query.xml");
        } catch (IOException e) {
            throw new Error(e);
        }
        NamedQueryXMLParser namedQueryXMLParser = new NamedQueryXMLParser();
        namedQueryXMLParser.parse(resources).forEach(repository::register);
    }
}
