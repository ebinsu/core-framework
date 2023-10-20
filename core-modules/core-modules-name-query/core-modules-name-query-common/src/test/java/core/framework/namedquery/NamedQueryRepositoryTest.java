package core.framework.namedquery;

import core.framework.namedquery.impl.NamedQueryRepositoryImpl;
import core.framework.namedquery.support.node.ForEachNode;
import core.framework.namedquery.support.node.IfNode;
import core.framework.namedquery.support.node.TextNode;
import core.framework.namedquery.support.node.sql.SqlNode;
import core.framework.namedquery.support.node.sql.WhereNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
public class NamedQueryRepositoryTest {
    public static String TEST_ID = "test_id";
    public static String TEST_NS = "test_ns";
    public static String NAME_VARIABLE = "test";

    @Test
    public void test_get_named_query() {
        TextNode textNode = new TextNode("and ${name}");
        IfNode ifNode = new IfNode(List.of(textNode), "name!=null");
        WhereNode whereNode = new WhereNode(List.of(ifNode));
        SqlNode sqlNode = new SqlNode(TEST_NS, TEST_ID, Map.class, List.of(whereNode));
        NamedQueryRepository namedQueryRepository = new NamedQueryRepositoryImpl();
        namedQueryRepository.register(sqlNode);
        NamedQuery namedQuery = namedQueryRepository.get(TEST_NS + "." + TEST_ID, Map.of("name", NAME_VARIABLE));
        Assertions.assertEquals("WHERE test", namedQuery.getQuery());
        Assertions.assertEquals(NAME_VARIABLE, namedQuery.getQueryParameter("name"));
    }

    @Test
    public void test_get_foreach_named_query() {
        List<Object> param = List.of("a", "b", "c");
        ForEachNode forEachNode = new ForEachNode("col", "(", ")", ",");
        SqlNode sqlNode = new SqlNode(TEST_NS, TEST_ID, Map.class, List.of(forEachNode));
        NamedQueryRepository namedQueryRepository = new NamedQueryRepositoryImpl();
        namedQueryRepository.register(sqlNode);
        NamedQuery namedQuery = namedQueryRepository.get(TEST_NS + "." + TEST_ID, Map.of("col", param));
        Assertions.assertEquals("( #{__collection__col__0} , #{__collection__col__1} , #{__collection__col__2} )", namedQuery.getQuery());
        Assertions.assertEquals("a", namedQuery.getQueryParameter("__collection__col__0"));
        Assertions.assertEquals("b", namedQuery.getQueryParameter("__collection__col__1"));
        Assertions.assertEquals("c", namedQuery.getQueryParameter("__collection__col__2"));
    }
}
