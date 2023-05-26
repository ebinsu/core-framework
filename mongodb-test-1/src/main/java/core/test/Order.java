package core.test;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
@Entity
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

    public Order(String id) {
        this.id = id;
    }

    public Order(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public void add(String e) {
        event.add(e);
    }

    public List<String> getEvent() {
        return event;
    }
}
