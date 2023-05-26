package core.test.mongo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;

import java.io.Serializable;

/**
 * @author ebin
 */
@Entity
@Table(name = "domain_event_tracking")
@NoSql(dataFormat = DataFormatType.MAPPED)
public class DomainEventTracking implements Serializable {

    @Id // Use generated OID (UUID) from Mongo.
    @GeneratedValue
    @Field(name = "_id")
    private String id;

    private String description;
}
