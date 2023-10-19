package core.framework.test.hibernate.query;

import core.framework.namedquery.NamedQueryService;
import core.framework.query.annotation.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ebin
 */
@Service
public class GetTestDomainNameQueryHandler {
    @Autowired
    private NamedQueryService nameQueryService;

    @QueryHandler
    public TestDomainDTO handle(GetTestDomainQuery query) {
        return (TestDomainDTO) nameQueryService.get("TestDomain.get", query).orElseThrow();
    }
}
