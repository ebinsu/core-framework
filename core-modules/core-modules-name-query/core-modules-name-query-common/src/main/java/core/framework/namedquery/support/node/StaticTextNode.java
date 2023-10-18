package core.framework.namedquery.support.node;

import core.framework.namedquery.support.NamedQueryContext;

/**
 * @author ebin
 */
public class StaticTextNode implements Node {
    private final String text;

    public StaticTextNode(String text) {
        this.text = text;
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        context.appendQuery(text);
        return true;
    }
}
