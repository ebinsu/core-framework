package core.framework.command.configuration;

import core.framework.command.CommandBus;
import core.framework.command.support.CommandBusImpl;
import core.framework.command.support.CommandHandlerAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class CommandConfiguration {
    @Bean
    public CommandBus commandBus() {
        return new CommandBusImpl();
    }

    @Bean
    public CommandHandlerAnnotationBeanPostProcessor commandHandlerAnnotationBeanPostProcessor() {
        return new CommandHandlerAnnotationBeanPostProcessor();
    }
}
