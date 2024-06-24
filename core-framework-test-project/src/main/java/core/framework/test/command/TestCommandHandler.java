package core.framework.test.command;

import core.framework.command.annotation.CommandHandler;
import core.framework.test.hibernate.domain.AssignIdDomain;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * @author ebin
 */
@Service
public class TestCommandHandler {
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    @CommandHandler
    public void handle(TestCommand testCommand) {
        AssignIdDomain assignIdDomain = new AssignIdDomain(1L);
        assignIdDomain.abc = 1L;
        entityManager.persist(assignIdDomain);
    }
}
