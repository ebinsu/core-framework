package core.framework.namedquery.configuration;


import core.framework.namedquery.NamedQueryBuilderProvider;
import core.framework.namedquery.NamedQueryExecutor;
import core.framework.namedquery.NamedQueryExecutorProvider;
import core.framework.namedquery.NamedQueryRepository;
import core.framework.namedquery.NamedQueryService;
import core.framework.namedquery.impl.NamedQueryRepositoryImpl;
import core.framework.namedquery.impl.NamedQueryRepositoryInitialize;
import core.framework.namedquery.impl.NamedQueryServiceImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;

/**
 * @author ebin
 */
@Configuration
@EnableConfigurationProperties({NamedQueryProperties.class})
public class NamedQueryConfiguration {
    @Bean
    public NamedQueryRepositoryInitialize namedQueryRepositoryInitialize() {
        return new NamedQueryRepositoryInitialize();
    }

    @Bean
    @ConditionalOnMissingBean
    public NamedQueryRepository nameQueryRepository(ObjectProvider<NamedQueryBuilderProvider> providers) {
        NamedQueryRepositoryImpl namedQueryRepository = new NamedQueryRepositoryImpl();
        providers.orderedStream().forEach(provider -> provider.get().forEach(namedQueryRepository::addNamedQueryBuilder));
        return namedQueryRepository;
    }

    @Bean
    @Primary
    public NamedQueryService nameQueryService(NamedQueryRepository nameQueryRepository,
                                              ObjectProvider<NamedQueryExecutorProvider> providers,
                                              NamedQueryProperties properties) {
        NamedQueryServiceImpl nameQueryService = new NamedQueryServiceImpl(nameQueryRepository, properties);
        providers.orderedStream().forEach(provider -> {
            Map<String, NamedQueryExecutor> map = provider.get();
            map.forEach(nameQueryService::register);
        });
        return nameQueryService;
    }
}
