package core.framework.namequery;

/**
 * @author ebin
 */
public enum QueryType {
    SQL, MONGODB;

    public static QueryType of(String node) {
        return switch (node) {
            case "sql" -> QueryType.SQL;
            case "mongo" -> QueryType.MONGODB;
            default -> throw new IllegalStateException("Unexpected value: " + node);
        };
    }
}
