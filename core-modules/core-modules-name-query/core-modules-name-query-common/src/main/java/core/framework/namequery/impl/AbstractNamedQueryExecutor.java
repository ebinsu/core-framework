package core.framework.namequery.impl;


import core.framework.namequery.NamedQuery;
import core.framework.namequery.NamedQueryExecutor;
import core.framework.namequery.support.parser.GenericTokenParser;
import core.framework.namequery.support.parser.IndexPlaceholderTokenHandler;
import core.framework.namequery.support.parser.TokenHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @author ebin
 */
public abstract class AbstractNamedQueryExecutor implements NamedQueryExecutor {

    @Override
    public <T> List<T> execute(NamedQuery namedQuery, Integer maxReturnRows) {
        String queryString = namedQuery.getQuery();
        IndexPlaceholderTokenHandler handler = new IndexPlaceholderTokenHandler();
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        queryString = parser.parse(queryString);
        List<Pair<String, Object>> queryParameter = handler.getParameterNames().stream().map(paramName ->
            Pair.of(paramName, namedQuery.getQueryParameter().get(paramName))
        ).toList();
        return doExecute(queryString, queryParameter, namedQuery, maxReturnRows);
    }

    protected abstract <T> List<T> doExecute(String queryString, List<Pair<String, Object>> queryParameter, NamedQuery namedQuery, Integer maxReturnRows);

    protected abstract TokenHandler getTokenHandler();
}
