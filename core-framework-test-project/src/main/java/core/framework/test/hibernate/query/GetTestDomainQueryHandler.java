package core.framework.test.hibernate.query;

import core.framework.query.Query;
import core.framework.query.QueryHandler;
import core.framework.query.support.namequery.NameQueryParam;
import core.framework.query.support.namequery.NameQueryServiceAdapter;
import org.springframework.stereotype.Service;

/**
 * @author ebin
 */
@Service
public class GetTestDomainQueryHandler extends NameQueryServiceAdapter implements QueryHandler<GetTestDomainQuery, TestDomainDTO> {

    @Override
    public TestDomainDTO handle(GetTestDomainQuery query) {
        NameQueryParam<TestDomainDTO> nameQueryParam = new NameQueryParam<>("TestDomain.get", TestDomainDTO.class);
        return this.get(nameQueryParam).orElseThrow();
    }
}
