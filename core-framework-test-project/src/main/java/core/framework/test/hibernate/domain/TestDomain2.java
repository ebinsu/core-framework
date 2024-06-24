package core.framework.test.hibernate.domain;

import core.framework.ddd.annotation.AggregateRoot;
import core.framework.jpa.common.AbstractAggregateRoot;
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
@Table(name = "test_2")
public class TestDomain2 extends AbstractAggregateRoot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private TestEntity entity;

    @Embedded
    private TestValueObject valueObject;

    public TestDomain2() {
        super("");
    }

    public TestDomain2(String name) {
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
}
