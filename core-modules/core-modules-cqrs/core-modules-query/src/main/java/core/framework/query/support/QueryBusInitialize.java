package core.framework.query.support;

import core.framework.query.QueryBus;
import core.framework.query.QueryHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

/**
 * @author ebin
 */
public class QueryBusInitialize implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        QueryBus queryBus = applicationContext.getBean(QueryBus.class);
        Map<String, QueryHandler> handlers = applicationContext.getBeansOfType(QueryHandler.class);
        handlers.values().forEach(queryBus::register);
    }
}
