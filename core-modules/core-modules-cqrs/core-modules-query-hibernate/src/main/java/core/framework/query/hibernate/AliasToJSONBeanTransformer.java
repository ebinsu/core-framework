package core.framework.query.hibernate;

import org.hibernate.query.TupleTransformer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Locale;

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
        Object result;
        try {
            result = resultClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < tuple.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                Field declaredField;
                try {
                    declaredField = resultClass.getDeclaredField(alias.toLowerCase(Locale.getDefault()));
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                try {
                    declaredField.set(result, tuple[i]);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        A a = A.class.getDeclaredConstructor().newInstance();
        Arrays.stream(A.class.getDeclaredFields()).forEach(f -> {
            try {
                f.set(a, "a");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(a.getA());
        System.out.println(a.getB());
    }

    public static class A {
        private String a;
        public String b;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }
    }
}
