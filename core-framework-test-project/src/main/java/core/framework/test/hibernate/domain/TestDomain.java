package core.framework.test.hibernate.domain;

import core.framework.jpa.hibernate.AbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author ebin
 */
@Entity
@Table(name = "test")
public class TestDomain extends AbstractAggregateRoot<TestDomain> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;

    public TestDomain() {
    }

    public TestDomain(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }
}
