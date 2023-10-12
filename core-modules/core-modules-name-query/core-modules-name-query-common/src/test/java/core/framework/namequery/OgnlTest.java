package core.framework.namequery;

import ognl.DefaultClassResolver;
import ognl.MemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * @author ebin
 */
public class OgnlTest {
    public static void main(String[] args) throws OgnlException {
        A a = new A("1");

        OgnlContext defaultContext = Ognl.createDefaultContext(new Object(), new DefaultMemberAccess(true), new DefaultClassResolver(), null);

        defaultContext.put("a", a);
        Object expression = Ognl.parseExpression("#a.id");
        Object result = Ognl.getValue(expression, defaultContext, defaultContext.getRoot());
        System.out.println(result);
    }

    public static class A {
        public String id;

        public A(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static class DefaultMemberAccess implements MemberAccess {
        private boolean allowPrivateAccess = false;
        private boolean allowProtectedAccess = false;
        private boolean allowPackageProtectedAccess = false;

        public DefaultMemberAccess(boolean allowAllAccess) {
            this(allowAllAccess, allowAllAccess, allowAllAccess);
        }

        public DefaultMemberAccess(boolean allowPrivateAccess, boolean allowProtectedAccess,
                                   boolean allowPackageProtectedAccess) {
            super();
            this.allowPrivateAccess = allowPrivateAccess;
            this.allowProtectedAccess = allowProtectedAccess;
            this.allowPackageProtectedAccess = allowPackageProtectedAccess;
        }


        @Override
        public Object setup(OgnlContext context, Object target, Member member, String propertyName) {
            Object result = null;

            if (isAccessible(context, target, member, propertyName)) {
                AccessibleObject accessible = (AccessibleObject) member;

                if (!accessible.isAccessible()) {
                    result = Boolean.TRUE;
                    accessible.setAccessible(true);
                }
            }
            return result;
        }

        @Override
        public void restore(OgnlContext context, Object target, Member member, String propertyName, Object state) {
            if (state != null) {
                ((AccessibleObject) member).setAccessible((Boolean) state);
            }
        }

        @Override
        public boolean isAccessible(OgnlContext context, Object target, Member member, String propertyName) {
            int modifiers = member.getModifiers();
            if (Modifier.isPublic(modifiers)) {
                return true;
            } else if (Modifier.isPrivate(modifiers)) {
                return this.allowPrivateAccess;
            } else if (Modifier.isProtected(modifiers)) {
                return this.allowProtectedAccess;
            } else {
                return this.allowPackageProtectedAccess;
            }
        }
    }

}
