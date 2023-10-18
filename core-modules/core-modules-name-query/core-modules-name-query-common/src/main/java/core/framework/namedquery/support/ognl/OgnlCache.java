package core.framework.namedquery.support.ognl;

import ognl.DefaultClassResolver;
import ognl.MemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.apache.ibatis.builder.BuilderException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ebin
 */
public final class OgnlCache {
    private static final MemberAccess MEMBER_ACCESS = new DefaultMemberAccess();
    private static final Map<String, Object> EXPRESSION_CACHE = new ConcurrentHashMap<>();

    private OgnlCache() {
    }

    public static Object getValue(String expression, Object root) {
        try {
            OgnlContext context = Ognl.createDefaultContext(root, MEMBER_ACCESS, new DefaultClassResolver(), null);
            return Ognl.getValue(parseExpression(expression), context, root);
        } catch (OgnlException e) {
            throw new BuilderException("Error evaluating expression '" + expression + "'. Cause: " + e, e);
        }
    }

    private static Object parseExpression(String expression) throws OgnlException {
        Object node = EXPRESSION_CACHE.get(expression);
        if (node == null) {
            node = Ognl.parseExpression(expression);
            EXPRESSION_CACHE.put(expression, node);
        }
        return node;
    }
}
