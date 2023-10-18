package core.framework.namedquery.support;

import core.framework.namedquery.support.node.BindNode;
import core.framework.namedquery.support.node.ForEachNode;
import core.framework.namedquery.support.node.IfNode;
import core.framework.namedquery.support.node.NodeBuilder;
import core.framework.namedquery.support.node.TextNodeBuilder;
import core.framework.namedquery.support.node.TrimNode;
import core.framework.namedquery.support.node.mongo.FilterNode;
import core.framework.namedquery.support.node.sql.WhereNode;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
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
        this.nodeBuilders = Map.of(
            "text", new TextNodeBuilder(),
            "bind", new BindNode.Builder(),
            "if", new IfNode.Builder(),
            "trim", new TrimNode.Builder(),
            "foreach", new ForEachNode.Builder(),
            "where", new WhereNode.Builder(),
            "filter", new FilterNode.Builder()
        );
    }

    public XPath getXpath() {
        return xpath;
    }

    public NodeBuilder getNodeBuilder(String name) {
        return nodeBuilders.get(name);
    }
}
