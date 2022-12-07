package core.framework.query.hibernate;

import framework.json.JSON;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.query.TupleTransformer;

import java.util.Map;

/**
 * @author ebin
 */
public class AliasToJSONBeanTransformer implements TupleTransformer<Object> {
    private final Class<?> resultClass;

    public AliasToJSONBeanTransformer(Class<?> resultClass) {
        this.resultClass = resultClass;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Map<String, Object> result = CollectionHelper.mapOfSize(tuple.length);
        for (int i = 0; i < tuple.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                result.put(alias.toLowerCase(), tuple[i]);
            }
        }
        return JSON.fromJSON(resultClass, JSON.toJSON(result));
    }
}
