package core.framework.namequery.impl;

import core.framework.namequery.QueryStatement;
import core.framework.namequery.QueryType;

/**
 * @author ebin
 */
public record QueryStatementImpl(String queryStatement, QueryType queryType) implements QueryStatement {
}
