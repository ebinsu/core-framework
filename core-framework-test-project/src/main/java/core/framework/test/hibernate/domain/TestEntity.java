package core.framework.test.hibernate.domain;

import core.framework.ddd.annotation.Entity;
import core.framework.jpa.common.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author ebin
 */
@Entity
@Table(name = "test_entity")
public class TestEntity extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    private String name;

    private TestEntity() {
    }

    public TestEntity(String name) {
        super("");
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }
}
