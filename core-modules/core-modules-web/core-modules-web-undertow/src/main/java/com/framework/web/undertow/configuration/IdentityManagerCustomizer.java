package com.framework.web.undertow.configuration;

import com.framework.web.undertow.security.AuthenticationRepository;
import com.framework.web.undertow.security.IdentityManagerImpl;
import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;

/**
 * @author ebin
 */
public class IdentityManagerCustomizer implements UndertowDeploymentInfoCustomizer {
    private final AuthenticationRepository repository;

    public IdentityManagerCustomizer(AuthenticationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        IdentityManagerImpl identityManager = new IdentityManagerImpl(repository);
        deploymentInfo.setIdentityManager(identityManager);
    }
}
