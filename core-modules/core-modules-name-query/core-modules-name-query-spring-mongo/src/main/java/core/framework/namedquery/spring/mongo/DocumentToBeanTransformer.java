package core.framework.namedquery.spring.mongo;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.bson.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author ebin
 */
public class DocumentToBeanTransformer {
    private final Class<?> resultClass;
    private Constructor<?> constructor;
    private final Map<String, ImmutablePair<Class<?>, Converter>> constructorParameters = new LinkedHashMap<>();
    private final Map<String, ImmutableTriple<Field, Class<?>, Converter>> fields = new LinkedHashMap<>();
    private boolean isMap = false;

    public DocumentToBeanTransformer(Class<?> resultClass) {
        this.resultClass = resultClass;
        if (resultClass.isAssignableFrom(Map.class)) {
            isMap = true;
        } else if (this.resultClass.isRecord()) {
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
                    if (field.getName().equals("id")) {
                        fields.put("_id", ImmutableTriple.of(field, fieldType, converter));
                    }
                });
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Object transform(Document document) {
        if (isMap) {
            return new HashMap<>(document);
        } else if (this.resultClass.isRecord()) {
            Object[] args = constructorParameters.entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    if (name.equals("id")) {
                        name = "_id";
                    }
                    ImmutablePair<Class<?>, Converter> pair = entry.getValue();
                    return pair.getRight().convert(pair.getLeft(), document.get(name));
                }).toArray();
            try {
                return this.constructor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Object o = this.constructor.newInstance();
                document.forEach((k, v) -> {
                    ImmutableTriple<Field, Class<?>, Converter> triple = fields.get(k);
                    if (triple != null) {
                        try {
                            Object value = ConvertUtils.convert(document.get(k), triple.getMiddle());
                            triple.getLeft().set(o, value);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                return o;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
