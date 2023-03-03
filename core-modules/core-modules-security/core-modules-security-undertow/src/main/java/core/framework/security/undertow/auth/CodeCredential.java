package core.framework.security.undertow.auth;

import io.undertow.security.idm.Credential;

/**
 * @author ebin
 */
public record CodeCredential(String code) implements Credential {

}
