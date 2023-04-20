package core.framework.namequery.impl;

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
public class MybatisNameQueryRepositoryInitialize implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        MybatisNameQueryRepository repository = applicationContext.getBean(MybatisNameQueryRepository.class);
        List<Resource> resources = null;
        try {
            resources = ResourcePatternResolverUtil.resolve("**/*Query.xml");
        } catch (IOException e) {
            throw new Error(e);
        }
        for (Resource resource : resources) {
            repository.addQueryResource(resource);
        }
        repository.initQueryTypes();
    }
}
