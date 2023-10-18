package core.framework.namedquery.support.node;


import core.framework.namedquery.support.NamedQueryContext;
import core.framework.namedquery.support.ognl.OgnlCache;
import core.framework.namedquery.support.parser.DynamicCheckerTokenHandler;
import core.framework.namedquery.support.parser.GenericTokenParser;
import core.framework.namedquery.support.parser.TokenHandler;

/**
 * @author ebin
 */
public class TextNode implements Node {
    private final String text;

    public TextNode(String text) {
        this.text = text;
    }

    public boolean isDynamic() {
        DynamicCheckerTokenHandler checker = new DynamicCheckerTokenHandler();
        GenericTokenParser parser = createParser(checker);
        parser.parse(text);
        return checker.isDynamic();
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        GenericTokenParser parser = createParser(new BindingTokenHandler(context));
        context.appendQuery(parser.parse(text));
        return true;
    }

    private GenericTokenParser createParser(TokenHandler handler) {
        return new GenericTokenParser("${", "}", handler);
    }

    private record BindingTokenHandler(NamedQueryContext context) implements TokenHandler {

        @Override
        public String handleToken(String content) {
            Object value = OgnlCache.getValue(content, context.getParameter());
            return value == null ? "" : String.valueOf(value);
        }
    }
}
