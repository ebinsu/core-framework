package core.framework.jpa.hibernate.support;

import org.hibernate.annotations.common.reflection.AnnotationReader;
import org.hibernate.annotations.common.reflection.MetadataProvider;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.Map;

/**
 * @author ebin
 */
public class JavaOverrideMetadataProvider implements MetadataProvider {
    @Override
    public Map<Object, Object> getDefaults() {
        return Collections.emptyMap();
    }

    @Override
    public AnnotationReader getAnnotationReader(AnnotatedElement annotatedElement) {
        return new JavaOverrideAnnotationReader(annotatedElement);
    }

    @Override
    public void reset() {
        //no-op
    }
}
