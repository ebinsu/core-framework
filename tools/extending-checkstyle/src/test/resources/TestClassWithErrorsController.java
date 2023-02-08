package core.framework.extending.checks;

import java.util.Map;

/**
 * @author ebin
 */
public class TestClassWithErrors {
    public Integer A;
    public Integer B;
    public String C;

    public TestClassWithErrors(String D) {
    }

    public static class ClassA {
        public void methodC(Map map, Object other, Map map2) {
            int i = 0;
            System.out.println(123);
        }
    }

    public void methodA(Map map, Object other, Map map2) {
        int i = 0;
        System.out.println(123);
    }

    public void methodB(String map, Integer intx) {
        String a = null;
    }
}
