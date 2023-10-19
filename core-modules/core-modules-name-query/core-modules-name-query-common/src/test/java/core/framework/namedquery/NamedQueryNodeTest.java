package core.framework.namedquery;

import core.framework.namedquery.support.NamedQueryContext;
import core.framework.namedquery.support.node.ChooseNode;
import core.framework.namedquery.support.node.ForEachNode;
import core.framework.namedquery.support.node.FragmentNode;
import core.framework.namedquery.support.node.IfNode;
import core.framework.namedquery.support.node.IncludeNode;
import core.framework.namedquery.support.node.OtherwiseNode;
import core.framework.namedquery.support.node.StaticTextNode;
import core.framework.namedquery.support.node.TextNode;
import core.framework.namedquery.support.node.TrimNode;
import core.framework.namedquery.support.node.sql.SqlNode;
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
        NamedQueryContext context = new NamedQueryContext("1", Map.of("name", NAME_VARIABLE), Map.of());
        textNode.apply(context);
        Assertions.assertEquals(NAME_VARIABLE, context.getQuery());
    }

    @Test
    public void test_if_node_condition_pass() {
        TextNode textNode = new TextNode("${name}");
        IfNode ifNode = new IfNode(List.of(textNode), "name!=null");
        NamedQueryContext context = new NamedQueryContext("1", Map.of("name", NAME_VARIABLE), Map.of());
        ifNode.apply(context);
        Assertions.assertEquals(NAME_VARIABLE, context.getQuery());
    }

    @Test
    public void test_if_node_condition_no_pass() {
        StaticTextNode textNode = new StaticTextNode(NAME_VARIABLE);
        IfNode ifNode = new IfNode(List.of(textNode), "name!=null");
        NamedQueryContext context = new NamedQueryContext("1", Map.of(), Map.of());
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
        NamedQueryContext context = new NamedQueryContext("1", Map.of("name", NAME_VARIABLE), Map.of());
        trimNode.apply(context);
        Assertions.assertEquals("prefix test override2 suffix", context.getQuery());
    }

    @Test
    public void test_where_node() {
        TextNode textNode = new TextNode("and ${name}");
        IfNode ifNode = new IfNode(List.of(textNode), "name!=null");
        WhereNode whereNode = new WhereNode(List.of(ifNode));
        NamedQueryContext context = new NamedQueryContext("1", Map.of("name", NAME_VARIABLE), Map.of());
        whereNode.apply(context);
        Assertions.assertEquals("WHERE test", QueryStringUtils.removeExtraWhitespaces(context.getQuery()));
    }

    @Test
    public void test_foreach_node() {
        ForEachNode forEachNode = new ForEachNode("col", "(", ")", ",");
        NamedQueryContext context = new NamedQueryContext("1", Map.of("col", List.of("a", "b", "c")), Map.of());
        forEachNode.apply(context);
        Assertions.assertEquals("( #{__collection__col__0} , #{__collection__col__1} , #{__collection__col__2} )", QueryStringUtils.removeExtraWhitespaces(context.getQuery()));
    }

    @Test
    public void test_choose_node() {
        StaticTextNode textNode = new StaticTextNode("if");
        IfNode ifNode = new IfNode(List.of(textNode), "name!=null");
        StaticTextNode otherwiseNodeTextNode = new StaticTextNode("otherwise");
        OtherwiseNode otherwiseNode = new OtherwiseNode(List.of(otherwiseNodeTextNode));
        ChooseNode chooseNode = new ChooseNode(List.of(ifNode), otherwiseNode);
        NamedQueryContext context = new NamedQueryContext("1", Map.of("name", NAME_VARIABLE), Map.of());
        chooseNode.apply(context);
        Assertions.assertEquals("if", context.getQuery());
    }

    @Test
    public void test_choose_node_otherwise() {
        StaticTextNode textNode = new StaticTextNode("if");
        IfNode ifNode = new IfNode(List.of(textNode), "name!=null");
        StaticTextNode otherwiseNodeTextNode = new StaticTextNode("otherwise");
        OtherwiseNode otherwiseNode = new OtherwiseNode(List.of(otherwiseNodeTextNode));
        ChooseNode chooseNode = new ChooseNode(List.of(ifNode), otherwiseNode);
        NamedQueryContext context = new NamedQueryContext("1", Map.of(), Map.of());
        chooseNode.apply(context);
        Assertions.assertEquals("otherwise", context.getQuery());
    }

    @Test
    public void test_include_node() {
        TextNode textNode = new TextNode("${name}");
        IfNode ifNode = new IfNode(List.of(textNode), "name!=null");
        FragmentNode fragmentNode = new FragmentNode("1", "f1", List.of(ifNode));

        IncludeNode includeNode = new IncludeNode("f1");
        StaticTextNode and = new StaticTextNode("and");
        SqlNode sqlNode = new SqlNode("1", "1", Object.class, List.of(includeNode, and, ifNode));
        NamedQueryContext context = new NamedQueryContext("1", Map.of("name", NAME_VARIABLE), Map.of("f1", fragmentNode));
        sqlNode.apply(context);
        Assertions.assertEquals("test and test", context.getQuery());
    }
}
