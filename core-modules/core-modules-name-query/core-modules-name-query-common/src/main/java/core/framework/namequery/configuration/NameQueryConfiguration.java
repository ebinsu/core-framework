package core.framework.namequery.configuration;


import core.framework.namequery.NameQueryExecutor;
import core.framework.namequery.NameQueryExecutorProvider;
import core.framework.namequery.NameQueryRepository;
import core.framework.namequery.impl.MybatisNameQueryRepository;
import core.framework.namequery.impl.MybatisNameQueryRepositoryInitialize;
import core.framework.namequery.impl.NameQueryServiceImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author ebin
 */
@Configuration
public class NameQueryConfiguration {

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
    public NameQueryServiceImpl nameQueryService(NameQueryRepository nameQueryRepository,
                                                 ObjectProvider<NameQueryExecutorProvider> providers) {
        NameQueryServiceImpl nameQueryService = new NameQueryServiceImpl(nameQueryRepository);
        providers.orderedStream().forEach(provider -> {
            NameQueryExecutor executor = provider.get();
            nameQueryService.addQueryExecutors(executor);
        });
        return nameQueryService;
    }
}
