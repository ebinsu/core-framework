package core.framework.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author ebin
 */
public class JSONTest {

    @Test
    public void test_json_ignore() {
        TestBean testBean = new TestBean("1", "1");
        String toJSON = JSON.toJSON(testBean);
        System.out.println(toJSON);
        TestBean fromJSON = JSON.fromJSON(TestBean.class, toJSON);
        Assertions.assertNull(fromJSON.getName());
    }

    public static class TestBean {
        private String id;
        @JsonIgnore
        private String name;

        private TestBean() {
        }

        public TestBean(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSex() {
            return null;
        }
    }
}
