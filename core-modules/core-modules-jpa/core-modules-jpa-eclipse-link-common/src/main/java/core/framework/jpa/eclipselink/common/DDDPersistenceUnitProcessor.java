package core.framework.jpa.eclipselink.common;

import core.framework.ddd.annotation.AggregateRoot;
import core.framework.ddd.annotation.Entity;
import core.framework.ddd.annotation.ValueObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

/**
 * @author ebin
 */
public class DDDPersistenceUnitProcessor {
    private DDDPersistenceUnitProcessor() {
    }

    public static boolean isAggregateRoot(MetadataClass candidateClass) {
        try {
            return Class.forName(candidateClass.getName()).isAnnotationPresent(AggregateRoot.class);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isEntity(MetadataClass candidateClass) {
        try {
            return Class.forName(candidateClass.getName()).isAnnotationPresent(Entity.class);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isValueObject(MetadataClass candidateClass) {
        try {
            return Class.forName(candidateClass.getName()).isAnnotationPresent(ValueObject.class);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
