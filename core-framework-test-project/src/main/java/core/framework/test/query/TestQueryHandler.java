package core.framework.test.query;

import core.framework.query.annotation.QueryHandler;
import org.springframework.stereotype.Service;

/**
 * @author ebin
 */
@Service
public class TestQueryHandler {
    @QueryHandler
    public String query(TestQuery testQuery) {
        return "test";
    }
}
