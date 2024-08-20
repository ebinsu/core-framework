package core.framework.namedquery.impl;

import java.util.Map;

/**
 * @author ebin
 */
public class SqlNamedQueryImpl extends AbstractNamedQuery {
    public SqlNamedQueryImpl(String xmlTagName, String name, String query, Map<String, Object> parameter, Class<?> resultClass) {
        super(xmlTagName, name, query, parameter, resultClass);
    }
}
