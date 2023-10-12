package core.framework.namequery.support.node;

import core.framework.namequery.support.NamedQueryContext;
import core.framework.namequery.support.ognl.OgnlCache;
import core.framework.namequery.support.parser.XMLNode;

/**
 * @author ebin
 */
public class BindNode implements Node {
    private final String name;
    private final String expression;

    public BindNode(String name, String exp) {
        this.name = name;
        this.expression = exp;
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        final Object value = OgnlCache.getValue(expression, context.getParameter());
        context.addParameter(name, value);
        return true;
    }

    public static class Builder implements NodeBuilder {

        @Override
        public Node build(XMLNode nodeToHandle) {
            String name = nodeToHandle.getAttributes().getProperty("name");
            String expression = nodeToHandle.getAttributes().getProperty("value");
            return new BindNode(name, expression);
        }
    }
}
