package core.framework.command.configuration;

import core.framework.command.support.CommandHandlerAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author ebin
 */
public class CommandHandlerBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(CommandHandlerAnnotationBeanPostProcessor.BEAN_NAME)) {
            registry.registerBeanDefinition(
                    CommandHandlerAnnotationBeanPostProcessor.BEAN_NAME,
                    new RootBeanDefinition(CommandHandlerAnnotationBeanPostProcessor.class)
            );
        }
    }
}
