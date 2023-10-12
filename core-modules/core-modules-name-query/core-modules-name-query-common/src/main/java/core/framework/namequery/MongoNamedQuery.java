package core.framework.namequery;

/**
 * @author ebin
 */
public interface MongoNamedQuery extends NamedQuery {
    String getReadPreference();
}
