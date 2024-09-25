package core.framework.namedquery.impl;

import core.framework.namedquery.NamedQuery;
import core.framework.namedquery.support.parser.ForEachParameterParser;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * @author ebin
 */
public abstract class AbstractNamedQuery implements NamedQuery {
    protected String name;
    protected String query;
    protected Map<String, Object> queryParameter;
    protected Class<?> resultClass;
    protected String xmlTagName;

    public AbstractNamedQuery(String xmlTagName, String name, String query, Map<String, Object> queryParameter, Class<?> resultClass) {
        this.name = name;
        this.query = query;
        this.queryParameter = queryParameter;
        this.resultClass = resultClass;
        this.xmlTagName = xmlTagName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Map<String, Object> getQueryParameters() {
        return Collections.unmodifiableMap(queryParameter);
    }

    @Override
    public String getQuery() {
        return this.query;
    }

    @Override
    public Object getQueryParameter(String parameterName) {
        Pair<String, Integer> foreachParam = ForEachParameterParser.parse(parameterName);
        if (foreachParam != null) {
            Collection<Object> c = (Collection<Object>) this.queryParameter.get(foreachParam.getLeft());
            Iterator<Object> iterator = c.iterator();
            int index = 0;
            Object param = null;
            while (iterator.hasNext()) {
                Object next = iterator.next();
                if (index == foreachParam.getRight()) {
                    param = next;
                    break;
                }
                index++;
            }
            return param;
        } else {
            return this.queryParameter.get(parameterName);
        }
    }

    @Override
    public boolean containsQueryParameter(String parameterName) {
        return this.queryParameter.containsKey(parameterName);
    }

    @Override
    public Class<?> getResultClass() {
        return this.resultClass;
    }

    @Override
    public String getXmlTagName() {
        return this.xmlTagName;
    }

    @Override
    public void setResultClass(Class<?> resultClass) {
        this.resultClass = resultClass;
    }
}
