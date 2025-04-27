package core.framework.web.common.configuration;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author ebin
 */
@Component
public class EnvironmentApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        Properties props = new Properties();
        props.put("spring.main.banner-mode", "off");
        props.put("server.shutdown", "graceful");
        event.getEnvironment().getPropertySources().addFirst(new PropertiesPropertySource("web-override-properties", props));
    }
}
