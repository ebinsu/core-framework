package core.framework.jpa.hibernate.support;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.cfg.annotations.reflection.internal.JPAXMLOverriddenAnnotationReader;
import org.hibernate.cfg.annotations.reflection.internal.XMLContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
public class ExtendJPAXMLOverrideAnnotationReader extends JPAXMLOverriddenAnnotationReader {
    protected final AnnotatedElement element;
    private transient Annotation[] annotations;
    private transient Map<Class<?>, Annotation> annotationsMap;

    public ExtendJPAXMLOverrideAnnotationReader(AnnotatedElement el, XMLContext xmlContext, BootstrapContext bootstrapContext) {
        super(el, xmlContext, bootstrapContext);
        this.element = el;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        overrideAnnotation();
        T annotation = super.getAnnotation(annotationType);
        if (annotation == null) {
            return (T) this.annotationsMap.get(annotationType);
        }
        return annotation;
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationType) {
        overrideAnnotation();
        if (!super.isAnnotationPresent(annotationType)) {
            return this.annotationsMap.containsKey(annotationType);
        }
        return true;
    }

    @Override
    public Annotation[] getAnnotations() {
        overrideAnnotation();
        return ArrayUtils.addAll(super.getAnnotations(), this.annotations);
    }

    public void overrideAnnotation() {
        if (annotations == null) {
            Annotation[] annotations = element.getAnnotations();
            List<Annotation> annotationList = new ArrayList<>(annotations.length);
            annotationsMap = new HashMap<>(annotations.length);
            for (Annotation annotation : annotations) {
                if (JavaOverrideAnnotationReader.ANNOTATION_OVERRIDDEN.containsKey(annotation.annotationType())) {
                    Class<?> aClass = JavaOverrideAnnotationReader.ANNOTATION_OVERRIDDEN.get(annotation.annotationType());
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
