package core.framework.query.configuration;

import core.framework.query.QueryBus;
import core.framework.query.support.QueryBusImpl;
import core.framework.query.support.QueryHandlerAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class QueryServiceConfiguration {
    @Bean
    public QueryHandlerAnnotationBeanPostProcessor queryHandlerAnnotationBeanPostProcessor() {
        return new QueryHandlerAnnotationBeanPostProcessor();
    }

    @Bean
    public QueryBus queryBus() {
        return new QueryBusImpl();
    }
}
