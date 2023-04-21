package core.framework.namequery.hibernate;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.hibernate.query.TupleTransformer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author ebin
 */
public class ReflectToBeanTransformer implements TupleTransformer<Object> {
    private final Class<?> resultClass;
    private final Constructor<?> constructor;
    private final Map<String, ImmutablePair<Class<?>, Converter>> constructorParameters = new LinkedHashMap<>();
    private final Map<String, ImmutableTriple<Field, Class<?>, Converter>> fields = new LinkedHashMap<>();

    public ReflectToBeanTransformer(Class<?> resultClass) {
        this.resultClass = resultClass;
        if (this.resultClass.isRecord()) {
            this.constructor = resultClass.getDeclaredConstructors()[0];
            Parameter[] parameters = this.constructor.getParameters();
            IntStream.range(0, parameters.length).forEach(parameterIndex -> {
                Parameter parameter = parameters[parameterIndex];
                Class<?> fieldType = parameter.getType();
                String fieldName = parameter.getName();
                Converter converter = ConvertUtils.lookup(fieldType);
                constructorParameters.put(fieldName, ImmutablePair.of(fieldType, converter));
            });
        } else {
            try {
                this.constructor = resultClass.getDeclaredConstructor();
                Field[] declaredFields = resultClass.getDeclaredFields();
                Arrays.stream(declaredFields).forEach(field -> {
                    Class<?> fieldType = field.getType();
                    Converter converter = ConvertUtils.lookup(fieldType);
                    field.setAccessible(true);
                    fields.put(field.getName(), ImmutableTriple.of(field, fieldType, converter));
                });
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        if (this.resultClass.isRecord()) {
            Object[] args = constructorParameters.entrySet().stream()
                    .map(entry ->
                            IntStream.range(0, aliases.length)
                                    .filter(tupleIndex -> entry.getKey().equals(aliases[tupleIndex]))
                                    .mapToObj(tupleIndex -> {
                                        ImmutablePair<Class<?>, Converter> pair = entry.getValue();
                                        return pair.getRight().convert(pair.getLeft(), tuple[tupleIndex]);
                                    })
                                    .findFirst()
                                    .orElse(null))
                    .toArray();
            try {
                return this.constructor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Object o = this.constructor.newInstance();
                for (int i = 0; i < tuple.length; i++) {
                    String alias = aliases[i];
                    ImmutableTriple<Field, Class<?>, Converter> triple = fields.get(alias);
                    if (triple != null) {
                        Object value = ConvertUtils.convert(tuple[i], triple.getMiddle());
                        triple.getLeft().set(o, value);
                    }
                }
                return o;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
