package core.framework.namedquery.impl;

import core.framework.namedquery.NamedQueryRepository;
import core.framework.namedquery.support.NamedQueryResourceHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * @author ebin
 */
public class NamedQueryRepositoryInitialize implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        NamedQueryRepository repository = applicationContext.getBean(NamedQueryRepository.class);
        NamedQueryResourceFinder namedQueryResourceFinder = applicationContext.getBean(NamedQueryResourceFinder.class);

        List<NamedQueryResourceHolder> namedQueryResourceHolders = namedQueryResourceFinder.getNamedQueryResourceHolders();
        namedQueryResourceHolders.forEach(holder -> {
            holder.getMixedNodes().forEach(repository::register);
            holder.getFragmentNodes().forEach(repository::register);
        });
    }
}
