package core.framework.security.undertow.authentication.mailecode;

import io.undertow.security.idm.Credential;

/**
 * @author ebin
 */
public record StringCredential(String credentialStr) implements Credential {

}
