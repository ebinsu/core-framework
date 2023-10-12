package core.framework.namequery.support;

import core.framework.namequery.support.node.BindNode;
import core.framework.namequery.support.node.FilterNode;
import core.framework.namequery.support.node.IfNode;
import core.framework.namequery.support.node.NodeBuilder;
import core.framework.namequery.support.node.TextNodeBuilder;
import core.framework.namequery.support.node.TrimNode;
import core.framework.namequery.support.node.WhereNode;

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
            "where", new WhereNode.Builder(),
            "filter", new FilterNode.Builder(),
            "trim", new TrimNode.Builder()
        );
    }

    public XPath getXpath() {
        return xpath;
    }

    public NodeBuilder getNodeBuilder(String name) {
        return nodeBuilders.get(name);
    }
}
