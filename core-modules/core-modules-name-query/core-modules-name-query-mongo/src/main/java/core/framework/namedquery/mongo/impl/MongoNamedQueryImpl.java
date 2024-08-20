package core.framework.namedquery.mongo.impl;

import core.framework.namedquery.impl.AbstractNamedQuery;
import core.framework.namedquery.mongo.MongoNamedQuery;

import java.util.Map;

/**
 * @author ebin
 */
public class MongoNamedQueryImpl extends AbstractNamedQuery implements MongoNamedQuery {
    public String readPreference;

    public MongoNamedQueryImpl(String xmlTagName, String name, String query, Map<String, Object> queryParameter, Class<?> resultClass, String readPreference) {
        super(xmlTagName, name, query, queryParameter, resultClass);
        this.readPreference = readPreference;
    }

    @Override
    public String getReadPreference() {
        return this.readPreference;
    }
}
