package core.framework.jpa.eclipselink.mongodb.support;

import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionFactory;
import org.eclipse.persistence.eis.EISAccessor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoConnectionFactory;
import org.eclipse.persistence.nosql.adapters.mongo.MongoConnectionSpec;

import java.util.Properties;

/**
 * @author ebin
 */
public class ExtendMongoConnectionSpec extends MongoConnectionSpec {
    @Override
    public Connection connectToDataSource(EISAccessor accessor, Properties properties) throws DatabaseException, ValidationException {
        if (this.connectionSpec == null) {
            this.connectionSpec = new ExtendMongoJCAConnectionSpec();
        }
        return super.connectToDataSource(accessor, properties);
    }

    @Override
    protected ConnectionFactory createMongoConnectionFactory() {
        try {
            Class.forName("com.mongodb.client.MongoDatabase");
            return new ExtendMongoDatabaseConnectionFactory();
        } catch (ClassNotFoundException e) {
            return new MongoConnectionFactory();
        }
    }
}
