package core.framework.query.configuration;

import core.framework.query.support.QueryHandlerAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author ebin
 */
public class QueryHandlerBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(QueryHandlerAnnotationBeanPostProcessor.BEAN_NAME)) {
            registry.registerBeanDefinition(
                    QueryHandlerAnnotationBeanPostProcessor.BEAN_NAME,
                    new RootBeanDefinition(QueryHandlerAnnotationBeanPostProcessor.class)
            );
        }
    }
}
