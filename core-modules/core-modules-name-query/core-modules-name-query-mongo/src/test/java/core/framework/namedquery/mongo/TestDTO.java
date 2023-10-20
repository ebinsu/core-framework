package core.framework.namedquery.mongo;

/**
 * @author ebin
 */
public record TestDTO(String id, String name) {

    @Override
    public String toString() {
        return "TestDTO{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
