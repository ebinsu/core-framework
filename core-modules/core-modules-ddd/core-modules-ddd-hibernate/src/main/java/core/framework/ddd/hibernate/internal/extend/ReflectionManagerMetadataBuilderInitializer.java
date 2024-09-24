package core.framework.ddd.hibernate.internal.extend;

import org.hibernate.annotations.common.reflection.MetadataProviderInjector;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataBuilderImplementor;
import org.hibernate.boot.spi.MetadataBuilderInitializer;

/**
 * @author ebin
 */
public class ReflectionManagerMetadataBuilderInitializer implements MetadataBuilderInitializer {
    @Override
    public void contribute(MetadataBuilder metadataBuilder, StandardServiceRegistry serviceRegistry) {
        if (metadataBuilder instanceof MetadataBuilderImplementor implementor) {
            ReflectionManager reflectionManager = implementor.getBootstrapContext().getReflectionManager();
            if (reflectionManager instanceof MetadataProviderInjector injector) {
                injector.setMetadataProvider(new ExtendJPAXMLOverrideMetadataProvider(implementor.getBootstrapContext()));
            }
        }
    }
}
