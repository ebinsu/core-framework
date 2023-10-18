package core.framework.namedquery;

import core.framework.namedquery.support.NamedQueryContext;
import core.framework.namedquery.support.node.ForEachNode;
import core.framework.namedquery.support.node.IfNode;
import core.framework.namedquery.support.node.StaticTextNode;
import core.framework.namedquery.support.node.TextNode;
import core.framework.namedquery.support.node.TrimNode;
import core.framework.namedquery.support.node.sql.WhereNode;
import core.framework.namedquery.support.parser.QueryStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
public class NamedQueryNodeTest {
    public static String NAME_VARIABLE = "test";

    @Test
    public void test_text_node() {
        TextNode textNode = new TextNode("${name}");
        Assertions.assertTrue(textNode.isDynamic());
        NamedQueryContext context = new NamedQueryContext(Map.of("name", NAME_VARIABLE));
        textNode.apply(context);
        Assertions.assertEquals(NAME_VARIABLE, context.getQuery());
    }

    @Test
    public void test_if_node_condition_pass() {
        TextNode textNode = new TextNode("${name}");
        IfNode ifNode = new IfNode(List.of(textNode), "name!=null");
        NamedQueryContext context = new NamedQueryContext(Map.of("name", NAME_VARIABLE));
        ifNode.apply(context);
        Assertions.assertEquals(NAME_VARIABLE, context.getQuery());
    }

    @Test
    public void test_if_node_condition_no_pass() {
        StaticTextNode textNode = new StaticTextNode(NAME_VARIABLE);
        IfNode ifNode = new IfNode(List.of(textNode), "name!=null");
        NamedQueryContext context = new NamedQueryContext(Map.of());
        ifNode.apply(context);
        Assertions.assertEquals(0, context.getQuery().length());
    }

    @Test
    public void test_trim_node() {
        String prefix = "prefix";
        String suffix = "suffix";
        TextNode textNode = new TextNode("override1${name} override2");
        IfNode ifNode = new IfNode(List.of(textNode), "name!=null");
        TrimNode trimNode = new TrimNode(List.of(ifNode), prefix, "override1", suffix, null, false);
        NamedQueryContext context = new NamedQueryContext(Map.of("name", NAME_VARIABLE));
        trimNode.apply(context);
        Assertions.assertEquals("prefix test override2 suffix", context.getQuery());
    }

    @Test
    public void test_where_node() {
        TextNode textNode = new TextNode("and ${name}");
        IfNode ifNode = new IfNode(List.of(textNode), "name!=null");
        WhereNode whereNode = new WhereNode(List.of(ifNode));
        NamedQueryContext context = new NamedQueryContext(Map.of("name", NAME_VARIABLE));
        whereNode.apply(context);
        Assertions.assertEquals("WHERE test", QueryStringUtils.removeExtraWhitespaces(context.getQuery()));
    }

    @Test
    public void test_foreach_node() {
        ForEachNode forEachNode = new ForEachNode("col", "(", ")", ",");
        NamedQueryContext context = new NamedQueryContext(Map.of("col", List.of("a", "b", "c")));
        forEachNode.apply(context);
        Assertions.assertEquals("( #{__collection__col__0} , #{__collection__col__1} , #{__collection__col__2} )", QueryStringUtils.removeExtraWhitespaces(context.getQuery()));
    }
}
