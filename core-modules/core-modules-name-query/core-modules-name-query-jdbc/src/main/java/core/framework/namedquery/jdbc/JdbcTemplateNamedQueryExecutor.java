package core.framework.namedquery.jdbc;

import core.framework.namedquery.NamedQuery;
import core.framework.namedquery.NamedQueryExecutor;
import core.framework.namedquery.support.parser.DefaultPlaceholderTokenHandler;
import core.framework.namedquery.support.parser.GenericTokenParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * @author ebin
 */
public class JdbcTemplateNamedQueryExecutor implements NamedQueryExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTemplateNamedQueryExecutor.class);
    private final JdbcTemplate jdbcTemplate;
    private final Map<Class<?>, RowMapper<?>> rowMappers = new ConcurrentHashMap<>();
    private final int batchSize;

    public JdbcTemplateNamedQueryExecutor(JdbcTemplate jdbcTemplate, int batchSize) {
        this.jdbcTemplate = jdbcTemplate;
        this.batchSize = batchSize;
    }

    @Override
    public <T> List<T> execute(NamedQuery namedQuery, Integer maxReturnRows) {
        String queryString = namedQuery.getQuery();
        DefaultPlaceholderTokenHandler handler = new DefaultPlaceholderTokenHandler();
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        queryString = parser.parse(queryString);
        Object[] params = handler.getParameterNames().stream().map(namedQuery::getQueryParameter).toArray();
        LOGGER.info("Named query: [{}], query string: [{}]", namedQuery.getName(), queryString);
        LOGGER.info("Query parameter: [{}]", params);
        String finalQueryString = queryString;
        return (List<T>) jdbcTemplate.query(con -> getPreparedStatement(maxReturnRows, params, finalQueryString, con),
            getTransformer(namedQuery.getResultClass()));
    }

    private PreparedStatement getPreparedStatement(Integer maxReturnRows, Object[] params, String finalQueryString, Connection con) throws SQLException {
        PreparedStatement preparedStatement = con.prepareStatement(finalQueryString);
        if (maxReturnRows != null) {
            preparedStatement.setMaxRows(maxReturnRows);
            preparedStatement.setFetchSize(Math.min(batchSize, maxReturnRows));
        } else {
            preparedStatement.setFetchSize(batchSize);
        }

        IntStream.range(0, params.length).forEach(index -> {
            try {
                preparedStatement.setObject(index + 1, params[index]);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return preparedStatement;
    }

    private <T> RowMapper<?> getTransformer(Class<T> beanType) {
        if (beanType.isAssignableFrom(Map.class)) {
            return rowMappers.computeIfAbsent(beanType, k -> new ColumnMapRowMapper());
        } else if (beanType.isRecord()) {
            return rowMappers.computeIfAbsent(beanType, k -> new DataClassRowMapper<>(beanType));
        } else {
            return rowMappers.computeIfAbsent(beanType, k -> new BeanPropertyRowMapper<>(beanType));
        }
    }
}
