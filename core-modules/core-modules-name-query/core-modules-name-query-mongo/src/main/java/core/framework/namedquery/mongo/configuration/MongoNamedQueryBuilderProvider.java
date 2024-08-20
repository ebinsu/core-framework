package core.framework.namedquery.mongo.configuration;

import core.framework.namedquery.NamedQueryBuilder;
import core.framework.namedquery.NamedQueryBuilderProvider;
import core.framework.namedquery.mongo.impl.MongoNamedQueryBuilder;

import java.util.List;

/**
 * @author ebin
 */
public class MongoNamedQueryBuilderProvider implements NamedQueryBuilderProvider {
    @Override
    public List<NamedQueryBuilder> get() {
        return List.of(new MongoNamedQueryBuilder());
    }
}
