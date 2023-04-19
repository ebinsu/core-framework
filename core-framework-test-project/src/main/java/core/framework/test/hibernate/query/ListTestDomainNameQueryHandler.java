package core.framework.test.hibernate.query;

import core.framework.query.annotation.QueryHandler;
import core.framework.query.support.NameQueryService;
import core.framework.query.support.namequery.NameQueryParamImpl;
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
