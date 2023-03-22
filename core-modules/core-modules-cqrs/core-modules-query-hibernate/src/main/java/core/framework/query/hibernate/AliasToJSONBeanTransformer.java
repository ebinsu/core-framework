package core.framework.query.hibernate;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.TupleTransformer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author ebin
 */
public class AliasToJSONBeanTransformer implements TupleTransformer<Object> {
    private static final String CAMEL_SPLIT = "_";
    private final Class<?> resultClass;

    public AliasToJSONBeanTransformer(Class<?> resultClass) {
        this.resultClass = resultClass;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Object result;
        try {
            result = resultClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException
                 | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < tuple.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                try {
                    Field declaredField = resultClass.getDeclaredField(camelCase(alias));
                    declaredField.set(result, tuple[i]);
                } catch (NoSuchFieldException
                         | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    private String camelCase(String input) {
        String output = input.toLowerCase(Locale.getDefault());
        if (output.contains(CAMEL_SPLIT)) {
            String[] split = output.split(CAMEL_SPLIT);
            output = IntStream.range(0, split.length).mapToObj(index -> {
                if (index > 0) {
                    return StringUtils.capitalize(split[index]);
                } else {
                    return split[index];
                }
            }).collect(Collectors.joining());
        }
        return output;
    }
}
