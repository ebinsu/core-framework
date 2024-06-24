package core.framework.test.hibernate.domain;

import core.framework.ddd.annotation.AggregateRoot;
import core.framework.jpa.common.AbstractAggregateRoot;
import core.framework.test.demo.domain.Demo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * @author ebin
 */
@AggregateRoot
@Table(name = "test")
public class TestDomain extends AbstractAggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;

    private String testName;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private TestEntity entity;

    @Embedded
    private TestValueObject valueObject;

    @OneToOne
    private Demo demo;

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

    public void setEntity(TestEntity entity) {
        this.entity = entity;
    }

    public void setValueObject(TestValueObject valueObject) {
        this.valueObject = valueObject;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public TestEntity getEntity() {
        return entity;
    }

    public TestValueObject getValueObject() {
        return valueObject;
    }

    public Demo getDemo() {
        return demo;
    }

    public void setDemo(Demo demo) {
        this.demo = demo;
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
