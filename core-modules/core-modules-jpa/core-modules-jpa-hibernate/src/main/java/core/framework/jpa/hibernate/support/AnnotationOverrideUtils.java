package core.framework.jpa.hibernate.support;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import org.hibernate.annotations.common.annotationfactory.AnnotationDescriptor;
import org.hibernate.annotations.common.annotationfactory.AnnotationFactory;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author ebin
 */
public final class AnnotationOverrideUtils {
    public static void override(Class<?> annotationClass, List<Annotation> annotationList) {
        if (annotationClass == Entity.class) {
            annotationList.add(getEntity());
        } else if (annotationClass == Embeddable.class) {
            annotationList.add(getEmbeddable());
        }
    }

    private static Entity getEntity() {
        AnnotationDescriptor entity = new AnnotationDescriptor(Entity.class);
        return AnnotationFactory.create(entity);
    }

    private static Embeddable getEmbeddable() {
        AnnotationDescriptor embeddable = new AnnotationDescriptor(Embeddable.class);
        return AnnotationFactory.create(embeddable);
    }
}
