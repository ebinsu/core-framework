package core.framework.test.hibernate.query;

import core.framework.query.annotation.QueryHandler;
import core.framework.query.support.namequery.NameQueryParamImpl;
import core.framework.query.support.namequery.NameQueryServiceAdapter;
import org.springframework.stereotype.Service;

/**
 * @author ebin
 */
@Service
public class GetTestDomainNameQueryHandler extends NameQueryServiceAdapter {

    @QueryHandler
    public TestDomainDTO handle(GetTestDomainQuery query) {
        NameQueryParamImpl<TestDomainDTO> nameQueryParamImpl = new NameQueryParamImpl<>("TestDomain.get", TestDomainDTO.class);
        return this.get(nameQueryParamImpl).orElseThrow();
    }
}
