package core.test.mongo;

/**
 * @author ebin
 */
public class MongoDBProperties {
    public static final String WRITE_CONCERN = "hibernate.ogm.mongodb.write_concern";
    public static final String WRITE_CONCERN_TYPE = "hibernate.ogm.mongodb.write_concern_type";
    public static final String READ_CONCERN_TYPE = "hibernate.ogm.mongodb.read_concern_type";
    public static final String READ_CONCERN = "hibernate.ogm.mongodb.read_concern";
    public static final String READ_PREFERENCE = "hibernate.ogm.mongodb.read_preference";
    public static final String ASSOCIATION_DOCUMENT_STORAGE = "hibernate.ogm.mongodb.association_document_storage";
    public static final String AUTHENTICATION_MECHANISM = "hibernate.ogm.mongodb.authentication_mechanism";
    public static final String AUTHENTICATION_DATABASE = "hibernate.ogm.mongodb.authentication_database";
    public static final String MONGO_DRIVER_SETTINGS_PREFIX = "hibernate.ogm.mongodb.driver";

    private MongoDBProperties() {
    }
}
