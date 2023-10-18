package core.framework.namedquery.support.ognl;

import ognl.AbstractMemberAccess;
import ognl.OgnlContext;

import java.lang.reflect.Member;

/**
 * @author ebin
 */
public class DefaultMemberAccess extends AbstractMemberAccess {
    @Override
    public boolean isAccessible(OgnlContext context, Object target, Member member, String propertyName) {
        return false;
    }
}
