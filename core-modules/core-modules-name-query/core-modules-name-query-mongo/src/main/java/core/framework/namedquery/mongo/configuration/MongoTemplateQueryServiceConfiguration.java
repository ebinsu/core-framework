package core.framework.namedquery.mongo.configuration;

import core.framework.namedquery.NamedQueryExecutor;
import core.framework.namedquery.NamedQueryExecutorProvider;
import core.framework.namedquery.configuration.NamedQueryProperties;
import core.framework.namedquery.mongo.MongoTemplateNamedQueryDatasourceProvider;
import core.framework.namedquery.mongo.MongoTemplateNamedQueryExecutor;
import core.framework.namedquery.mongo.impl.node.MongoNodeBuilderProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author ebin
 */
@Configuration
public class MongoTemplateQueryServiceConfiguration {
    public static final String MONGO_TEMPLATE_NAMED_QUERY_EXECUTOR = "mongoTemplateNamedQueryExecutor";

    @Bean(name = MONGO_TEMPLATE_NAMED_QUERY_EXECUTOR)
    public NamedQueryExecutor mongoTemplateNamedQueryExecutor(MongoTemplateNamedQueryDatasourceProvider provider,
                                                              NamedQueryProperties namedQueryProperties) {
        return new MongoTemplateNamedQueryExecutor(provider.get(), namedQueryProperties.getBatchSize());
    }

    @Bean
    public NamedQueryExecutorProvider mongoTemplateNamedQueryExecutorProvider(@Autowired @Qualifier(MONGO_TEMPLATE_NAMED_QUERY_EXECUTOR) NamedQueryExecutor mongoTemplateNamedQueryExecutor) {
        return () -> Map.of("mongo", mongoTemplateNamedQueryExecutor);
    }

    @Bean
    public MongoNodeBuilderProvider mongoNodeBuilderProvider() {
        return new MongoNodeBuilderProvider();
    }

    @Bean
    public MongoNamedQueryBuilderProvider mongoNamedQueryBuilderProvider() {
        return new MongoNamedQueryBuilderProvider();
    }
}
