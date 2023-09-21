package core.framework.mongodb.configuration;

import com.mongodb.MongoClientSettings;
import core.framework.bson.ExtendCodecRegistry;
import core.framework.mongodb.MongodbDomainEventStoreImpl;
import core.framework.mongodb.support.ExtendMongoTemplate;
import core.framework.mongodb.support.MongodbDomainEventDispatcher;
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
    public MongodbDomainEventStoreImpl mongodbDomainEventStore(ExtendMongoTemplate extendMongoTemplate) {
        return new MongodbDomainEventStoreImpl(extendMongoTemplate);
    }

    @Bean
    public MongodbDomainEventDispatcher mongodbDomainEventDispatcher(MongodbDomainEventStoreImpl mongodbDomainEventStore) {
        return new MongodbDomainEventDispatcher(mongodbDomainEventStore);
    }

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return clientSettingsBuilder -> clientSettingsBuilder.codecRegistry(
            CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                ExtendCodecRegistry.codecRegistry())
        );
    }
}
