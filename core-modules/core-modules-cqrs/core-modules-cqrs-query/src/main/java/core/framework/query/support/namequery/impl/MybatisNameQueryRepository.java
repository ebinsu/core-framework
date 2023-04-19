package core.framework.query.support.namequery.impl;

import core.framework.query.support.namequery.NameQueryRepository;
import core.framework.query.support.namequery.QueryStatement;
import core.framework.query.support.namequery.QueryType;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
public class MybatisNameQueryRepository implements NameQueryRepository {
    private final Logger logger = LoggerFactory.getLogger(MybatisNameQueryRepository.class);
    private final Configuration configuration = new Configuration();
    private Map<String, QueryType> queryTypes;

    public void addQueryResource(Resource resource) {
        if (resource == null)
            return;
        try {
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(resource.getInputStream(), this.configuration,
                    resource.toString(), this.configuration.getSqlFragments());
            xmlMapperBuilder.parse();
            logger.info("Add query file. Filename = " + resource.getFilename());
        } catch (Exception e) {
            throw new Error("Failed to parse mapping resource: '" + resource + "'", e);
        }
    }

    public void initQueryTypes() {
        Map<String, QueryType> queryTypes = new HashMap<>();
        List<String> queryNames = this.configuration.getMappedStatementNames().stream().filter(f -> f.contains(".")).toList();
        queryNames.forEach(queryName -> {
            // TODO The query is sql or nosql
            queryTypes.put(queryName, QueryType.SQL);
        });

        this.queryTypes = Collections.unmodifiableMap(queryTypes);
    }

    @Override
    public QueryStatement getQueryStatement(String queryName, Map<String, Object> param) {
        MappedStatement statement = this.configuration.getMappedStatement(queryName);
        if (statement != null) {
            BoundSql boudSql = statement.getBoundSql(param);
            QueryType queryType = getQueryType(queryName);
            return new QueryStatementImpl(boudSql.getSql(), queryType);
        } else {
            throw new RuntimeException("Query statement: [" + queryName + "] not found");
        }
    }

    private QueryType getQueryType(String queryName) {
        QueryType queryType = queryTypes.get(queryName);
        if (queryType == null) {
            throw new RuntimeException("Query statement: [\" + queryName + \"] not found");
        }
        return queryType;
    }
}
