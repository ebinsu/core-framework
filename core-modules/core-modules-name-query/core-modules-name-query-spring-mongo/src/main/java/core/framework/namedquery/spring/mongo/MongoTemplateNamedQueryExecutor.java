package core.framework.namedquery.spring.mongo;

import com.mongodb.ReadPreference;
import core.framework.namedquery.MongoNamedQuery;
import core.framework.namedquery.NamedQuery;
import core.framework.namedquery.NamedQueryExecutor;
import core.framework.namedquery.support.parser.GenericTokenParser;
import core.framework.namedquery.support.parser.IndexPlaceholderTokenHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.util.json.ParameterBindingDocumentCodec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public class MongoTemplateNamedQueryExecutor implements NamedQueryExecutor {
    MongoTemplate mongoTemplate;
    private final Map<Class<?>, DocumentToBeanTransformer> resultBeanTransformers = new ConcurrentHashMap<>();

    public MongoTemplateNamedQueryExecutor(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public <T> List<T> execute(NamedQuery namedQuery, Integer maxReturnRows) {
        String queryString = namedQuery.getQuery();
        IndexPlaceholderTokenHandler handler = new IndexPlaceholderTokenHandler();
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        queryString = parser.parse(queryString);
        List<Pair<String, Object>> queryParameter = handler.getParameterNames().stream().map(paramName ->
            Pair.of(paramName, namedQuery.getQueryParameter(paramName))
        ).toList();
        Map<String, Object> param = new HashMap<>(queryParameter.size());
        queryParameter.forEach(pair -> param.put(pair.getLeft(), pair.getRight()));

        MongoNamedQuery mongoNamedQuery = (MongoNamedQuery) namedQuery;
        ReadPreference readPreference = ReadPreference.valueOf(mongoNamedQuery.getReadPreference());
        ParameterBindingDocumentCodec parameterBindingDocumentCodec = new ParameterBindingDocumentCodec();
        Document document = parameterBindingDocumentCodec.decode(queryString, queryParameter.stream().map(Pair::getRight).toArray());
        Document result = mongoTemplate.executeCommand(document, readPreference);
        System.out.println(result);
        // find ok, cursor.firstBatch
        // count ok, n
        if (result.getDouble("ok") == 1) {
            Document cursor = result.get("cursor", Document.class);
            return (List<T>) cursor.get("firstBatch", List.class).stream().map(row -> {
                if (row instanceof Document rowDoc) {
                    return getTransformer(namedQuery.getResultClass()).transform(rowDoc);
                } else {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return List.of();
    }

    private <T> DocumentToBeanTransformer getTransformer(Class<T> beanType) {
        return resultBeanTransformers.computeIfAbsent(beanType, k -> new DocumentToBeanTransformer(beanType));
    }
}
