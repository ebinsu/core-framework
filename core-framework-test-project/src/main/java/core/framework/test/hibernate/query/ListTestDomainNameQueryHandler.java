package core.framework.test.hibernate.query;

import core.framework.namequery.NameQueryService;
import core.framework.namequery.impl.NameQueryParamImpl;
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
    private NameQueryService nameQueryService;

    @Transactional
    @QueryHandler
    public List<TestDomainDTO> handle(ListTestDomainQuery query) {
        NameQueryParamImpl<TestDomainDTO> nameQueryParamImpl = new NameQueryParamImpl<>("TestDomain.get", TestDomainDTO.class);
        return nameQueryService.select(nameQueryParamImpl);
    }
}
