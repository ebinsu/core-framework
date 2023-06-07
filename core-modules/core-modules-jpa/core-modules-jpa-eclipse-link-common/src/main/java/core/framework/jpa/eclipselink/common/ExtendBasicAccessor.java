package core.framework.jpa.eclipselink.common;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

/**
 * @author ebin
 */
public class ExtendBasicAccessor extends BasicAccessor {
    public ExtendBasicAccessor() {
    }

    public ExtendBasicAccessor(String xmlElement) {
        super(xmlElement);
    }

    public ExtendBasicAccessor(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);
    }

    @Override
    protected boolean isTimeClass(MetadataClass cls) {
        return cls.extendsClass(java.time.LocalDateTime.class) ||
            cls.extendsClass(java.time.LocalDate.class) ||
            cls.extendsClass(java.time.LocalTime.class) ||
            cls.extendsClass(java.time.OffsetDateTime.class) ||
            cls.extendsClass(java.time.OffsetTime.class) ||
            cls.extendsClass(java.time.ZonedDateTime.class);
    }
}
