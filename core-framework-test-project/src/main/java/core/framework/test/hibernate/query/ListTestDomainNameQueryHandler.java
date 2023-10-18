package core.framework.test.hibernate.query;

import core.framework.namedquery.NamedQueryService;
import core.framework.query.annotation.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ebin
 */
@Service
public class ListTestDomainNameQueryHandler {
    @Autowired
    private NamedQueryService nameQueryService;

    @Transactional
    @QueryHandler
    public List<TestDomainDTO> handle(ListTestDomainQuery query) {
        return nameQueryService.select("TestDomain.get", query);
    }
}
