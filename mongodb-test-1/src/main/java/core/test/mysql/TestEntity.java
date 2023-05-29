package core.test.mysql;

import core.framework.jpa.common.AbstractEntity;
import core.test.TestDomain;
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
@Table(name = "test_entity")
public class TestEntity extends AbstractEntity<TestDomain, String> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    private String name;

    public TestEntity() {
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
