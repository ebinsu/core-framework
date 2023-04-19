package core.framework.query.configuration;

import core.framework.query.QueryBus;
import core.framework.query.support.QueryBusImpl;
import core.framework.query.support.QueryHandlerAnnotationBeanPostProcessor;
import core.framework.query.support.namequery.NameNameQueryService;
import core.framework.query.support.namequery.NameQueryExecutor;
import core.framework.query.support.namequery.NameQueryExecutorProvider;
import core.framework.query.support.namequery.NameQueryRepository;
import core.framework.query.support.namequery.impl.MybatisNameQueryRepository;
import core.framework.query.support.namequery.impl.MybatisNameQueryRepositoryInitialize;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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

    @Bean
    public MybatisNameQueryRepositoryInitialize mybatisNameQueryRepositoryInitialize() {
        return new MybatisNameQueryRepositoryInitialize();
    }

    @Bean
    @ConditionalOnMissingBean
    public NameQueryRepository nameQueryRepository() {
        return new MybatisNameQueryRepository();
    }

    @Bean
    @Primary
    public NameNameQueryService nameQueryService(NameQueryRepository nameQueryRepository,
                                                 ObjectProvider<NameQueryExecutorProvider> providers) {
        NameNameQueryService nameQueryService = new NameNameQueryService(nameQueryRepository);
        providers.orderedStream().forEach(provider -> {
            NameQueryExecutor executor = provider.get();
            nameQueryService.addQueryExecutors(executor);
        });
        return nameQueryService;
    }
}
