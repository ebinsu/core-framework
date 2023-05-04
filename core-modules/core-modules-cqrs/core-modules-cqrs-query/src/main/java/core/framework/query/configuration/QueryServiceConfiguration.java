package core.framework.query.configuration;

import core.framework.query.QueryBus;
import core.framework.query.support.QueryBusImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ebin
 */
@Configuration
@Import(QueryHandlerBeanDefinitionRegistrar.class)
public class QueryServiceConfiguration {
    @Bean
    public QueryBus queryBus() {
        return new QueryBusImpl();
    }
}
