package core.framework.command.configuration;

import core.framework.command.CommandBus;
import core.framework.command.support.CommandBusImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ebin
 */
@Configuration
@Import(CommandHandlerBeanDefinitionRegistrar.class)
public class CommandConfiguration {
    @Bean
    public CommandBus commandBus() {
        return new CommandBusImpl();
    }
}
