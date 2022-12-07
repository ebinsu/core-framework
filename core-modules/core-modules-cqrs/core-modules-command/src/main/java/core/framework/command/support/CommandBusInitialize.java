package core.framework.command.support;

import core.framework.command.CommandBus;
import core.framework.command.CommandHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

/**
 * @author ebin
 */
public class CommandBusInitialize implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        CommandBus commandBus = applicationContext.getBean(CommandBus.class);
        Map<String, CommandHandler> handlers = applicationContext.getBeansOfType(CommandHandler.class);
        handlers.values().forEach(commandBus::register);
    }
}
