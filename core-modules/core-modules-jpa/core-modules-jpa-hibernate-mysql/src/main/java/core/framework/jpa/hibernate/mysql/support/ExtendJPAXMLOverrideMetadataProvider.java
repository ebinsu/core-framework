package core.framework.jpa.hibernate.mysql.support;

import org.hibernate.annotations.common.reflection.AnnotationReader;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.cfg.annotations.reflection.internal.JPAXMLOverriddenMetadataProvider;

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public class ExtendJPAXMLOverrideMetadataProvider extends JPAXMLOverriddenMetadataProvider {
    private final BootstrapContext bootstrapContext;
    private Map<AnnotatedElement, AnnotationReader> cache;

    public ExtendJPAXMLOverrideMetadataProvider(BootstrapContext bootstrapContext) {
        super(bootstrapContext);
        this.bootstrapContext = bootstrapContext;
    }

    @Override
    public AnnotationReader getAnnotationReader(AnnotatedElement annotatedElement) {
        if (cache == null) {
            cache = new HashMap<>(50);
        }
        AnnotationReader reader = cache.get(annotatedElement);
        if (reader == null) {
            if (getXMLContext().hasContext()) {
                reader = new ExtendJPAXMLOverrideAnnotationReader(annotatedElement, getXMLContext(), bootstrapContext);
            } else {
                reader = new JavaOverrideAnnotationReader(annotatedElement);
            }
            cache.put(annotatedElement, reader);
        }
        return reader;
    }

    @Override
    public void reset() {
        this.cache = null;
    }
}
