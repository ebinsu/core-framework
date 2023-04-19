package core.framework.test;

import core.framework.query.QueryBus;
import core.framework.test.query.TestQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ebin
 */
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class QueryTest {
    @Autowired
    private QueryBus queryBus;

    @Test
    void test_test_query() {
        String result = queryBus.dispatch(new TestQuery());
        Assertions.assertEquals("test", result);
    }
}
