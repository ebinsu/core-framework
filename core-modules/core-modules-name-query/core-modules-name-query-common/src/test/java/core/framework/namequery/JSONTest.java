package core.framework.namequery;

import core.framework.json.JSON;
import core.framework.json.JSONMapper;

import java.util.Map;

/**
 * @author ebin
 */
public class JSONTest {
    public static void main(String[] args) {
        Map map = JSONMapper.OBJECT_MAPPER.convertValue(new TestQuery(), Map.class);
        System.out.println(map);
    }

    public static class TestQuery {
        private String id = "id";
        public String name = "name";
        private EmbedQuery embedQuery = new EmbedQuery();
    }

    public static class EmbedQuery {
        private String id = "id";
        public String name = "name";
    }

}
