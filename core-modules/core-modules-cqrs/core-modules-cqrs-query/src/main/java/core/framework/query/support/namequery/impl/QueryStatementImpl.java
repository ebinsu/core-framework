package core.framework.query.support.namequery.impl;

import core.framework.query.support.namequery.QueryStatement;
import core.framework.query.support.namequery.QueryType;

/**
 * @author ebin
 */
public record QueryStatementImpl(String queryStatement, QueryType queryType) implements QueryStatement {
}
