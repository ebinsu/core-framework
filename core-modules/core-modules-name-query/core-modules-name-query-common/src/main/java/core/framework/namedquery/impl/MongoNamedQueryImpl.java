package core.framework.namedquery.impl;

import core.framework.namedquery.MongoNamedQuery;
import core.framework.namedquery.QueryType;

import java.util.Map;

/**
 * @author ebin
 */
public class MongoNamedQueryImpl extends AbstractNamedQuery implements MongoNamedQuery {
    public String readPreference;

    public MongoNamedQueryImpl(String name, String query, Map<String, Object> queryParameter, Class<?> resultClass, String readPreference) {
        super(name, query, queryParameter, resultClass);
        this.readPreference = readPreference;
    }

    @Override
    public QueryType getQueryType() {
        return QueryType.MONGODB;
    }

    @Override
    public String getReadPreference() {
        return this.readPreference;
    }
}
