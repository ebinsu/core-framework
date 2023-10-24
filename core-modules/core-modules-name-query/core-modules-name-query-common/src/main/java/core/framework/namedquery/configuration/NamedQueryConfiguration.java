package core.framework.namedquery.configuration;


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
    public NamedQueryRepository nameQueryRepository() {
        return new NamedQueryRepositoryImpl();
    }

    @Bean
    @Primary
    public NamedQueryService nameQueryService(NamedQueryRepository nameQueryRepository,
                                              ObjectProvider<NamedQueryExecutorProvider> providers,
                                              NamedQueryProperties properties) {
        NamedQueryServiceImpl nameQueryService = new NamedQueryServiceImpl(nameQueryRepository, properties.getDefaultMaxReturnSize());
        providers.orderedStream().forEach(provider -> nameQueryService.register(provider.get().getLeft(), provider.get().getRight()));
        return nameQueryService;
    }
}
