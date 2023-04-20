package core.framework.query.hibernate;

import org.apache.commons.beanutils.ConvertUtils;
import org.hibernate.query.TupleTransformer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.stream.IntStream;

/**
 * @author ebin
 */
public class ReflectToBeanTransformer implements TupleTransformer<Object> {
    private final Class<?> resultClass;

    public ReflectToBeanTransformer(Class<?> resultClass) {
        this.resultClass = resultClass;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        if (resultClass.isRecord()) {
            Constructor<?> constructor = resultClass.getDeclaredConstructors()[0];
            Parameter[] parameters = constructor.getParameters();
            Object[] args = new Object[parameters.length];
            IntStream.range(0, parameters.length).forEach(parameterIndex -> {
                Parameter parameter = parameters[parameterIndex];
                Class<?> fieldType = parameter.getType();
                String fieldName = parameter.getName();
                IntStream.range(0, aliases.length)
                        .filter(tupleIndex -> fieldName.equals(aliases[tupleIndex]))
                        .findFirst()
                        .ifPresent(tupleIndex -> {
                            Object value = tuple[tupleIndex];
                            if (!fieldType.isAssignableFrom(value.getClass())) {
                                value = ConvertUtils.convert(tuple[tupleIndex], fieldType);
                            }
                            args[parameterIndex] = value;
                        });
            });
            try {
                return constructor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Object o = resultClass.getDeclaredConstructor().newInstance();
                for (int i = 0; i < tuple.length; i++) {
                    String alias = aliases[i];
                    if (alias != null) {
                        try {
                            Field declaredField = resultClass.getDeclaredField(alias);
                            Class<?> fieldType = declaredField.getType();
                            Object value = tuple[i];
                            if (!fieldType.isAssignableFrom(value.getClass())) {
                                value = ConvertUtils.convert(tuple[i], fieldType);
                            }
                            declaredField.setAccessible(true);
                            declaredField.set(o, value);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                return o;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                     | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
