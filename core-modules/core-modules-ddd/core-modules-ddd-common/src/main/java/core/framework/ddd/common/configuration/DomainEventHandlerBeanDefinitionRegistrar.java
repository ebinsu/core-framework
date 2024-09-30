package core.framework.ddd.common.configuration;

import core.framework.ddd.common.event.DomainEventHandlerAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author ebin
 */
public class DomainEventHandlerBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(DomainEventHandlerAnnotationBeanPostProcessor.BEAN_NAME)) {
            registry.registerBeanDefinition(
                DomainEventHandlerAnnotationBeanPostProcessor.BEAN_NAME,
                new RootBeanDefinition(DomainEventHandlerAnnotationBeanPostProcessor.class)
            );
        }
    }
}
