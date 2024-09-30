package core.framework.ddd.mongodb.configuration;

import com.mongodb.MongoClientSettings;
import core.framework.bson.ExtendCodecRegistry;
import core.framework.ddd.api.DomainEventBus;
import core.framework.ddd.mongodb.MongodbDomainEventDispatcher;
import core.framework.ddd.mongodb.MongodbDomainEventStorageImpl;
import core.framework.ddd.mongodb.support.ExtendMongoTemplate;
import org.bson.codecs.configuration.CodecRegistries;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class MongodbConfiguration {
    @Bean
    public ExtendMongoTemplate extendMongoTemplate(MongoDatabaseFactory databaseFactory, MappingMongoConverter converter) {
        return new ExtendMongoTemplate(databaseFactory, converter);
    }

    @Bean
    public MongodbDomainEventStorageImpl mongodbDomainEventStore(ExtendMongoTemplate extendMongoTemplate) {
        return new MongodbDomainEventStorageImpl(extendMongoTemplate);
    }

    @Bean
    public MongodbDomainEventDispatcher mongodbDomainEventDispatcher(MongodbDomainEventStorageImpl mongodbDomainEventStore, DomainEventBus domainEventBus) {
        return new MongodbDomainEventDispatcher(mongodbDomainEventStore, domainEventBus);
    }

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return clientSettingsBuilder -> clientSettingsBuilder.codecRegistry(
            CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                ExtendCodecRegistry.codecRegistry())
        );
    }
}
