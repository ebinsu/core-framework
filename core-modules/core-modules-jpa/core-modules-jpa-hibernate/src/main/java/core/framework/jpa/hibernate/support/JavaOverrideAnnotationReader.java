package core.framework.jpa.hibernate.support;

import core.framework.ddd.annotation.AggregateRoot;
import core.framework.ddd.annotation.ValueObject;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.annotations.common.reflection.AnnotationReader;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
public class JavaOverrideAnnotationReader implements AnnotationReader {
    public static final Map<Class<?>, Class<?>> ANNOTATION_OVERRIDDEN = Map.of(
            AggregateRoot.class, Entity.class,
            core.framework.ddd.annotation.Entity.class, Entity.class,
            ValueObject.class, Embeddable.class
    );

    protected final AnnotatedElement element;
    private transient Annotation[] annotations;
    private transient Map<Class<?>, Annotation> annotationsMap;

    public JavaOverrideAnnotationReader(AnnotatedElement el) {
        this.element = el;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        initAnnotations();
        return (T) annotationsMap.get(annotationType);
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationType) {
        initAnnotations();
        return annotationsMap.containsKey(annotationType);
    }

    @Override
    public Annotation[] getAnnotations() {
        initAnnotations();
        return ArrayUtils.clone(annotations);
    }

    private Annotation[] getPhysicalAnnotations() {
        return element.getAnnotations();
    }

    private void initAnnotations() {
        if (annotations == null) {
            Annotation[] annotations = getPhysicalAnnotations();
            List<Annotation> annotationList = new ArrayList<>(annotations.length);
            annotationsMap = new HashMap<>(annotations.length);
            for (Annotation annotation : annotations) {
                if (ANNOTATION_OVERRIDDEN.containsKey(annotation.annotationType())) {
                    Class<?> aClass = ANNOTATION_OVERRIDDEN.get(annotation.annotationType());
                    AnnotationOverrideUtils.override(annotations, aClass, annotationList);
                } else {
                    annotationList.add(annotation);
                }
            }
            this.annotations = annotationList.toArray(new Annotation[0]);
            for (Annotation ann : this.annotations) {
                annotationsMap.put(ann.annotationType(), ann);
            }
        }
    }
}
