package core.test;

import core.framework.jpa.common.AbstractAggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author ebin
 */
@Entity
@Table(name = "test")
@NoSql(dataFormat = DataFormatType.MAPPED)
public class TestDomain extends AbstractAggregateRoot<TestDomain, String> {

    @Id // Use generated OID (UUID) from Mongo.
    @GeneratedValue
    @Field(name = "_id")
    private String id;

    public LocalDateTime testTime;

    private String name;

    private String testName;

    public Integer integerNum = 1;

    public Double doubleNum = 1.1d;

    public Float floatNum = 1.2f;

    public Long longNum = 1L;

    public BigDecimal bigDecimalNum = BigDecimal.ONE;

    public TestDomain() {
        super("");
    }

    public TestDomain(String name) {
        super("");
        this.name = name;
    }

    public LocalDateTime getTestTime() {
        return testTime;
    }

    public void setTestTime(LocalDateTime testTime) {
        this.testTime = testTime;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }


    public Integer getIntegerNum() {
        return integerNum;
    }

    public void setIntegerNum(Integer integerNum) {
        this.integerNum = integerNum;
    }

    public Double getDoubleNum() {
        return doubleNum;
    }

    public void setDoubleNum(Double doubleNum) {
        this.doubleNum = doubleNum;
    }

    public Float getFloatNum() {
        return floatNum;
    }

    public void setFloatNum(Float floatNum) {
        this.floatNum = floatNum;
    }

    public Long getLongNum() {
        return longNum;
    }

    public void setLongNum(Long longNum) {
        this.longNum = longNum;
    }
}
