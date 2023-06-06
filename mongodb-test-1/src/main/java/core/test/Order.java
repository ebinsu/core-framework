package core.test;

import core.framework.ddd.annotation.AggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.bson.types.ObjectId;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
@AggregateRoot
@Table(name = "orders")
@NoSql(dataFormat = DataFormatType.MAPPED)
public class Order implements Serializable {

    @Id // Use generated OID (UUID) from Mongo.
    @GeneratedValue
    @Field(name = "_id")
    private String id;

    private String description;

    @Transient
    private transient List<String> event = new ArrayList<>();

    public Order() {
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getEvent() {
        return event;
    }
}
