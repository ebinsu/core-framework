package core.framework.namequery.support.node;

import core.framework.namequery.support.NamedQueryContext;
import core.framework.namequery.support.parser.ChildrenNodeHelper;
import core.framework.namequery.support.parser.TrimHelper;
import core.framework.namequery.support.parser.XMLNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * @author ebin
 */
public class TrimNode implements Node {
    private final List<Node> childrenNodes;
    private final String prefix;
    private final String suffix;
    private final List<String> prefixesToOverride;
    private final List<String> suffixesToOverride;
    private final boolean forceApply;

    public TrimNode(List<Node> childrenNodes,
                    String prefix, String prefixesToOverride,
                    String suffix, String suffixesToOverride,
                    boolean forceApply) {
        this(childrenNodes, prefix, parseOverrides(prefixesToOverride), suffix, parseOverrides(suffixesToOverride), forceApply);
    }

    public TrimNode(List<Node> childrenNodes,
                    String prefix, List<String> prefixesToOverride,
                    String suffix, List<String> suffixesToOverride,
                    boolean forceApply) {
        this.childrenNodes = childrenNodes;
        this.prefix = prefix;
        this.prefixesToOverride = prefixesToOverride;
        this.suffix = suffix;
        this.suffixesToOverride = suffixesToOverride;
        this.forceApply = forceApply;
    }

    protected List<Node> getChildrenNodes() {
        return childrenNodes;
    }

    protected String getPrefix() {
        return prefix;
    }

    protected String getSuffix() {
        return suffix;
    }

    protected List<String> getPrefixesToOverride() {
        return prefixesToOverride;
    }

    protected List<String> getSuffixesToOverride() {
        return suffixesToOverride;
    }

    private static List<String> parseOverrides(String overrides) {
        if (overrides != null) {
            final StringTokenizer parser = new StringTokenizer(overrides, "|", false);
            final List<String> list = new ArrayList<>(parser.countTokens());
            while (parser.hasMoreTokens()) {
                list.add(parser.nextToken().toUpperCase(Locale.ENGLISH));
            }
            return list;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        NamedQueryContext childrenContext = new NamedQueryContext(context.getParameter());
        childrenNodes.forEach(node -> node.apply(childrenContext));

        TrimHelper util = new TrimHelper(childrenContext.getQuery(), prefix, prefixesToOverride, suffix, suffixesToOverride);
        context.appendQuery(util.trim(forceApply));
        return true;
    }

    public static class Builder implements NodeBuilder {

        @Override
        public Node build(XMLNode nodeToHandle) {
            List<Node> childrenNodes = ChildrenNodeHelper.build(nodeToHandle);
            String prefix = nodeToHandle.getAttributes().getProperty("prefix");
            String prefixOverrides = nodeToHandle.getAttributes().getProperty("prefixOverrides");
            String suffix = nodeToHandle.getAttributes().getProperty("suffix");
            String suffixOverrides = nodeToHandle.getAttributes().getProperty("suffixOverrides");
            return new TrimNode(childrenNodes, prefix, prefixOverrides, suffix, suffixOverrides, false);
        }
    }
}
