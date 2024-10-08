package core.framework.ddd.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author ebin
 */
@Document("test")
public class TestEntity extends AbstractAggregateRoot {
    @Id
    private String id;

    private String name;

    private LocalDateTime localDateTime;

    private LocalDate localDate;

    private BigDecimal bigDecimal;

    private Integer integer;

    public TestEntity() {
    }

    public TestEntity(String name) {
        this.name = name;
        this.localDateTime = LocalDateTime.now();
        this.localDate = LocalDate.now();
        this.bigDecimal = BigDecimal.ONE;
        this.integer = 100;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public Integer getInteger() {
        return integer;
    }
}
