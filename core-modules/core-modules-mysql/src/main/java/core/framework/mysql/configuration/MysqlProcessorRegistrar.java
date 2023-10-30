package core.framework.mysql.configuration;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author ebin
 */
public class MysqlProcessorRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(MysqlDataSourceBeanPostProcessor.BEAN_NAME)) {
            registry.registerBeanDefinition(
                MysqlDataSourceBeanPostProcessor.BEAN_NAME,
                new RootBeanDefinition(MysqlDataSourceBeanPostProcessor.class)
            );
        }
    }
}