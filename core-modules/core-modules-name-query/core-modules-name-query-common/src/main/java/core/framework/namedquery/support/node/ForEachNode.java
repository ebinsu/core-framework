package core.framework.namedquery.support.node;

import core.framework.namedquery.support.NamedQueryContext;
import core.framework.namedquery.support.ognl.ExpressionEvaluator;
import core.framework.namedquery.support.parser.ForEachParameterParser;
import core.framework.namedquery.support.parser.XMLNode;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * <foreach collection="list" open="(" close=")" separator="," />
 *
 * @author ebin
 */
public class ForEachNode implements Node {
    private final String collection;
    private final String open;
    private final String close;
    private final String separator;

    public ForEachNode(String collection, String open, String close, String separator) {
        this.collection = collection;
        this.open = open;
        this.close = close;
        this.separator = Optional.ofNullable(separator).orElse("");
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        Map<String, Object> parameters = context.getParameter();
        final Iterable<?> iterable = ExpressionEvaluator.evaluateIterable(collection, parameters, false);
        if (iterable == null || !iterable.iterator().hasNext()) {
            return true;
        }
        boolean first = true;
        applyOpen(context);
        int i = 0;
        Iterator<?> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            if (!first) {
                context.appendQuery(separator);
            }
            context.appendQuery("#{" + ForEachParameterParser.get(collection, i) + "}");
            first = false;
            i++;
        }
        applyClose(context);
        return true;
    }


    private void applyOpen(NamedQueryContext context) {
        if (open != null) {
            context.appendQuery(open);
        }
    }

    private void applyClose(NamedQueryContext context) {
        if (close != null) {
            context.appendQuery(close);
        }
    }

    public static class Builder implements NodeBuilder {

        @Override
        public Node build(XMLNode nodeToHandle) {
            String collection = nodeToHandle.getAttributes().getProperty("collection");
            String open = nodeToHandle.getAttributes().getProperty("open");
            String close = nodeToHandle.getAttributes().getProperty("close");
            String separator = nodeToHandle.getAttributes().getProperty("separator");

            return new ForEachNode(collection, open, close, separator);
        }
    }
}
