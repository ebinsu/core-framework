package core.framework.namedquery.impl;

import core.framework.namedquery.QueryType;

import java.util.Map;

/**
 * @author ebin
 */
public class SqlNamedQueryImpl extends AbstractNamedQuery {
    public SqlNamedQueryImpl(String name, String query, Map<String, Object> parameter, Class<?> resultClass) {
        super(name, query, parameter, resultClass);
    }

    @Override
    public QueryType getQueryType() {
        return QueryType.SQL;
    }
}
