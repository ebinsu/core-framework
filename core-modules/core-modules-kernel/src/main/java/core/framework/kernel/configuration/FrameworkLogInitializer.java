package core.framework.kernel.configuration;

import core.framework.kernel.log.FrameworkLogConfigurator;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author ebin
 */
public class FrameworkLogInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        FrameworkLogConfigurator.configure();
    }
}
