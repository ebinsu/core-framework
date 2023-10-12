package core.framework.namedquery.spring.mongo;

import com.mongodb.ReadPreference;
import core.framework.namequery.MongoNamedQuery;
import core.framework.namequery.NamedQuery;
import core.framework.namequery.impl.AbstractNamedQueryExecutor;
import core.framework.namequery.support.parser.TokenHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.util.json.ParameterBindingDocumentCodec;

import java.util.List;

/**
 * @author ebin
 */
public class MongoTemplateNamedQueryExecutor extends AbstractNamedQueryExecutor {
    MongoTemplate mongoTemplate;

    @Override
    protected <T> List<T> doExecute(String queryString, List<Pair<String, Object>> queryParameter, NamedQuery namedQuery, Integer maxReturnRows) {
        MongoNamedQuery mongoNamedQuery = (MongoNamedQuery) namedQuery;
        ReadPreference readPreference = ReadPreference.valueOf(mongoNamedQuery.getReadPreference());
        ParameterBindingDocumentCodec parameterBindingDocumentCodec = new ParameterBindingDocumentCodec();
        Document document = parameterBindingDocumentCodec.decode(queryString, queryParameter.stream().map(Pair::getRight).toArray());
        Document result = mongoTemplate.executeCommand(document, readPreference);

        // find ok, cursor.firstBatch
        // count ok, n
        return null;
    }

    @Override
    protected TokenHandler getTokenHandler() {
        return null;
    }
}
