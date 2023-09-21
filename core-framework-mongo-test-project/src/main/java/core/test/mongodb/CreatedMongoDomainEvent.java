package core.test.mongodb;

import core.framework.ddd.support.AbstractDomainEvent;
import org.bson.types.ObjectId;

/**
 * @author ebin
 */
public class CreatedMongoDomainEvent extends AbstractDomainEvent<MongoDomain, ObjectId> {
}
