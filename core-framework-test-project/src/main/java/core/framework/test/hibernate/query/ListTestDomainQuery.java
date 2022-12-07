package core.framework.test.hibernate.query;

import core.framework.query.Query;

import java.util.List;

/**
 * @author ebin
 */
public class ListTestDomainQuery implements Query<List<TestDomainDTO>> {
    public String id;
}
