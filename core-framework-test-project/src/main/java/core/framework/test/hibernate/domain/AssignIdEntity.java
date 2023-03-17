package core.framework.test.hibernate.domain;

import core.framework.ddd.annotation.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author ebin
 */
@Entity
@Table(name = "AssignIdEntity")
public class AssignIdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
}
