package core.framework.jpa.eclipselink.mongodb.support;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import core.framework.bson.ExtendCodecRegistry;
import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionSpec;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoCodecs;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoDatabaseConnection;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoDatabaseConnectionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
public class ExtendMongoDatabaseConnectionFactory extends MongoDatabaseConnectionFactory {
    @Override
    public Connection getConnection(ConnectionSpec spec) throws ResourceException {
        ExtendMongoJCAConnectionSpec connectionSpec = (ExtendMongoJCAConnectionSpec) spec;
        MongoDatabase db = this.db;
        boolean isExternal = true;
        if (db == null) {
            List<ServerAddress> servers = new ArrayList<ServerAddress>();
            for (int index = 0; index < connectionSpec.getHosts().size(); index++) {
                String host = connectionSpec.getHosts().get(index);
                int port = ServerAddress.defaultPort();
                if (connectionSpec.getPorts().size() > index) {
                    port = connectionSpec.getPorts().get(index);
                }
                ServerAddress server = new ServerAddress(host, port);
                servers.add(server);
            }
            if (connectionSpec.getHosts().isEmpty()) {
                ServerAddress server = new ServerAddress("localhost", ServerAddress.defaultPort());
                servers.add(server);
            }
            MongoClient mongo = this.mongo;
            if (mongo == null) {
                isExternal = false;
                List<MongoCredential> credentialsList = new ArrayList<>();
                if ((connectionSpec.getUser() != null) && (connectionSpec.getUser().length() > 0)) {
                    MongoCredential credential = null;

                    if (connectionSpec.getAuthSource() != null) {
                        credential = MongoCredential.createCredential(connectionSpec.getUser(), connectionSpec.getAuthSource(), connectionSpec.getPassword());
                    } else {
                        credential = MongoCredential.createCredential(connectionSpec.getUser(), connectionSpec.getDB(), connectionSpec.getPassword());
                    }

                    credentialsList.add(credential);
                }
                MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder();
                optionsBuilder.serverSelectionTimeout(connectionSpec.getServerSelectionTimeout());
                if (connectionSpec.getReadPreference() != null) {
                    optionsBuilder.readPreference(connectionSpec.getReadPreference());
                }
                if (connectionSpec.getWriteConcern() != null) {
                    optionsBuilder.writeConcern(connectionSpec.getWriteConcern());
                }

                optionsBuilder.codecRegistry(CodecRegistries.fromRegistries(ExtendCodecRegistry.codecRegistry(), MongoCodecs.codecRegistry()));
                MongoClientOptions options = optionsBuilder.build();
                if (servers.isEmpty()) {
                    mongo = new MongoClient(new ServerAddress(), credentialsList, options);
                } else {
                    mongo = new MongoClient(servers, credentialsList, options);
                }
            }
            db = mongo.getDatabase(connectionSpec.getDB());
            try {
                db.runCommand(new Document("ping", 1)); // check connection
            } catch (Exception exception) {
                ResourceException resourceException = new ResourceException(exception);
                resourceException.initCause(exception);
                throw resourceException;
            }
        }

        return new MongoDatabaseConnection(mongo, db, isExternal, connectionSpec);
    }
}
