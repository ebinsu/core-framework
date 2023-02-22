package core.framework.test.hibernate.domain;

import core.framework.ddd.annotation.ValueObject;
import jakarta.persistence.Column;

/**
 * @author ebin
 */
@ValueObject
public class TestValueObject {
    @Column(name = "voname")
    private String value;

    public TestValueObject() {
    }

    public TestValueObject(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
