package core.framework.ddd;

/**
 * @author ebin
 */
public interface Entity<T extends AggregateRoot<T, ?>, ID> extends Identifiable<ID> {
}
