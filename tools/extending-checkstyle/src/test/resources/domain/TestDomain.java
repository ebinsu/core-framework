package core.framework.test.hibernate.domain;

import core.framework.ddd.annotation.AggregateRoot;
import core.framework.jpa.hibernate.AbstractAggregateRoot;
import core.framework.test.demo.domain.Demo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * @author ebin
 */
@AggregateRoot
@Table(name = "test")
public class TestDomain extends AbstractAggregateRoot<TestDomain, Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private TestEntity entity;

    @Embedded
    private TestValueObject valueObject;

    @OneToOne
    private Demo demo;

    public TestDomain() {
    }

    public TestDomain(String name) {
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
}
