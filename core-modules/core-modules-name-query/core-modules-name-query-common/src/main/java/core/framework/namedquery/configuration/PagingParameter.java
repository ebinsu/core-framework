package core.framework.namedquery.configuration;

/**
 * @author ebin
 */
public class PagingParameter {
    private String start = "start";
    private String limit = " limit";

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }
}
