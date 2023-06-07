package core.framework.jpa.eclipselink.mongodb.support;

import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoJCAConnectionSpec;

/**
 * @author ebin
 */
public class ExtendMongoJCAConnectionSpec extends MongoJCAConnectionSpec {
    @Override
    public char[] getPassword() {
        return super.getPassword();
    }
}
