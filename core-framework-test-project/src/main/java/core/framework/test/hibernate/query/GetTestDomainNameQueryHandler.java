package core.framework.test.hibernate.query;

import core.framework.namequery.NameQueryService;
import core.framework.namequery.impl.NameQueryParamImpl;
import core.framework.query.annotation.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ebin
 */
@Service
public class GetTestDomainNameQueryHandler {
    @Autowired
    private NameQueryService nameQueryService;

    @QueryHandler
    public TestDomainDTO handle(GetTestDomainQuery query) {
        NameQueryParamImpl<TestDomainDTO> nameQueryParamImpl = new NameQueryParamImpl<>("TestDomain.get", TestDomainDTO.class);
        return nameQueryService.get(nameQueryParamImpl).orElseThrow();
    }
}
