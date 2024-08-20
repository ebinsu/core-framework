package core.framework.namedquery.support;

import core.framework.namedquery.support.node.BindNode;
import core.framework.namedquery.support.node.ChooseNode;
import core.framework.namedquery.support.node.ForEachNode;
import core.framework.namedquery.support.node.FragmentNode;
import core.framework.namedquery.support.node.IfNode;
import core.framework.namedquery.support.node.IncludeNode;
import core.framework.namedquery.support.node.NodeBuilder;
import core.framework.namedquery.support.node.OtherwiseNode;
import core.framework.namedquery.support.node.TextNodeBuilder;
import core.framework.namedquery.support.node.sql.SqlNode;
import core.framework.namedquery.support.node.sql.WhereNode;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public class ResolverContext {
    private final XPath xpath;
    private final Map<String, NodeBuilder> nodeBuilders;

    public ResolverContext() {
        XPathFactory factory = XPathFactory.newInstance();
        this.xpath = factory.newXPath();
        this.nodeBuilders = new HashMap<>(11);
        nodeBuilders.put("text", new TextNodeBuilder());
        nodeBuilders.put("bind", new BindNode.Builder());
        nodeBuilders.put("if", new IfNode.Builder());
        nodeBuilders.put("choose", new ChooseNode.Builder());
        nodeBuilders.put("when", new IfNode.Builder());
        nodeBuilders.put("otherwise", new OtherwiseNode.Builder());
        nodeBuilders.put("include", new IncludeNode.Builder());
        nodeBuilders.put("foreach", new ForEachNode.Builder());
        nodeBuilders.put("where", new WhereNode.Builder());
        nodeBuilders.put("sql", new SqlNode.Builder());
        nodeBuilders.put("fragment", new FragmentNode.Builder());
    }

    public XPath getXpath() {
        return xpath;
    }

    public NodeBuilder getNodeBuilder(String name) {
        return nodeBuilders.get(name);
    }

    public void register(String name, NodeBuilder builder) {
        this.nodeBuilders.put(name, builder);
    }
}
