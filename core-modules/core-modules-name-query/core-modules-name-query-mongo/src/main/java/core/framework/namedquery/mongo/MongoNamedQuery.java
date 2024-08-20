package core.framework.namedquery.mongo;

import core.framework.namedquery.NamedQuery;

/**
 * @author ebin
 */
public interface MongoNamedQuery extends NamedQuery {
    String getReadPreference();
}
