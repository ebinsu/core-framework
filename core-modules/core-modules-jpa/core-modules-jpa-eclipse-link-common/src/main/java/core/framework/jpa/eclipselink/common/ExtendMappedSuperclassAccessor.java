package core.framework.jpa.eclipselink.common;

import org.eclipse.persistence.annotations.Array;
import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.BasicMap;
import org.eclipse.persistence.annotations.Structure;
import org.eclipse.persistence.annotations.Transformation;
import org.eclipse.persistence.annotations.VariableOneToOne;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.DerivedIdClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ElementCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedIdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.TransformationAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VariableOneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VersionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.structures.ArrayAccessor;
import org.eclipse.persistence.internal.jpa.metadata.structures.StructureAccessor;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_BASIC;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ELEMENT_COLLECTION;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_EMBEDDED;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_EMBEDDED_ID;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ID;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MANY_TO_MANY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MANY_TO_ONE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ONE_TO_MANY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ONE_TO_ONE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_VERSION;

/**
 * @author ebin
 */
public class ExtendMappedSuperclassAccessor extends MappedSuperclassAccessor {
    public ExtendMappedSuperclassAccessor() {
    }

    public ExtendMappedSuperclassAccessor(String xmlElement) {
        super(xmlElement);
    }

    public ExtendMappedSuperclassAccessor(MetadataAnnotation annotation, MetadataClass cls, MetadataProject project) {
        super(annotation, cls, project);
    }

    public ExtendMappedSuperclassAccessor(MetadataAnnotation annotation, MetadataClass cls, MetadataDescriptor descriptor) {
        super(annotation, cls, descriptor);
    }

    @Override
    protected MappingAccessor buildAccessor(MetadataAnnotatedElement accessibleObject) {
        if (accessibleObject.isBasicCollection(this)) {
            return new BasicCollectionAccessor(accessibleObject.getAnnotation(BasicCollection.class), accessibleObject, this);
        } else if (accessibleObject.isBasicMap(this)) {
            return new BasicMapAccessor(accessibleObject.getAnnotation(BasicMap.class), accessibleObject, this);
        } else if (accessibleObject.isArray(this)) {
            return new ArrayAccessor(accessibleObject.getAnnotation(Array.class), accessibleObject, this);
        } else if (accessibleObject.isElementCollection(this)) {
            return new ElementCollectionAccessor(accessibleObject.getAnnotation(JPA_ELEMENT_COLLECTION), accessibleObject, this);
        } else if (accessibleObject.isVersion(this)) {
            return new VersionAccessor(accessibleObject.getAnnotation(JPA_VERSION), accessibleObject, this);
        } else if (accessibleObject.isId(this) && !accessibleObject.isDerivedId(this)) {
            return new IdAccessor(accessibleObject.getAnnotation(JPA_ID), accessibleObject, this);
        } else if (accessibleObject.isDerivedIdClass(this)) {
            return new DerivedIdClassAccessor(accessibleObject, this);
        } else if (accessibleObject.isBasic(this)) {
            return new BasicAccessor(accessibleObject.getAnnotation(JPA_BASIC), accessibleObject, this);
        } else if (accessibleObject.isStructure(this)) {
            return new StructureAccessor(accessibleObject.getAnnotation(Structure.class), accessibleObject, this);
        } else if (accessibleObject.isEmbedded(this)) {
            return new EmbeddedAccessor(accessibleObject.getAnnotation(JPA_EMBEDDED), accessibleObject, this);
        } else if (accessibleObject.isEmbeddedId(this)) {
            return new EmbeddedIdAccessor(accessibleObject.getAnnotation(JPA_EMBEDDED_ID), accessibleObject, this);
        } else if (accessibleObject.isTransformation(this)) {
            return new TransformationAccessor(accessibleObject.getAnnotation(Transformation.class), accessibleObject, this);
        } else if (accessibleObject.isManyToMany(this)) {
            return new ManyToManyAccessor(accessibleObject.getAnnotation(JPA_MANY_TO_MANY), accessibleObject, this);
        } else if (accessibleObject.isManyToOne(this)) {
            return new ManyToOneAccessor(accessibleObject.getAnnotation(JPA_MANY_TO_ONE), accessibleObject, this);
        } else if (accessibleObject.isOneToMany(this)) {
            // A OneToMany can default and doesn't require an annotation to be present.
            return new OneToManyAccessor(accessibleObject.getAnnotation(JPA_ONE_TO_MANY), accessibleObject, this);
        } else if (accessibleObject.isOneToOne(this)) {
            // A OneToOne can default and doesn't require an annotation to be present.
            return new OneToOneAccessor(accessibleObject.getAnnotation(JPA_ONE_TO_ONE), accessibleObject, this);
        } else if (accessibleObject.isVariableOneToOne(this)) {
            // A VariableOneToOne can default and doesn't require an annotation to be present.
            return new VariableOneToOneAccessor(accessibleObject.getAnnotation(VariableOneToOne.class), accessibleObject, this);
        } else if (excludeDefaultMappings()) {
            return null;
        } else {
            // Default case (everything else falls into a Basic)
            return new ExtendBasicAccessor(accessibleObject.getAnnotation(JPA_BASIC), accessibleObject, this);
        }
    }
}
