package core.framework.namedquery;

/**
 * @author ebin
 */
public interface MongoNamedQuery extends NamedQuery {
    String getReadPreference();
}
