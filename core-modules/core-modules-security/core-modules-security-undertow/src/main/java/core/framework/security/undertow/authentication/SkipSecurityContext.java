package core.framework.security.undertow.authentication;

import io.undertow.util.AttachmentKey;

/**
 * @author ebin
 */
public final class SkipSecurityContext {
    public static final AttachmentKey<Boolean> ATTACHMENT_KEY = AttachmentKey.create(Boolean.class);

    private SkipSecurityContext() {
    }
}
