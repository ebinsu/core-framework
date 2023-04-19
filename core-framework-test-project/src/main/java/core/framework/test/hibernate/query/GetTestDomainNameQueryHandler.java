package core.framework.test.hibernate.query;

import core.framework.query.annotation.QueryHandler;
import core.framework.query.support.namequery.NameNameQueryParam;
import core.framework.query.support.namequery.NameNameQueryServiceAdapter;
import org.springframework.stereotype.Service;

/**
 * @author ebin
 */
@Service
public class GetTestDomainNameQueryHandler extends NameNameQueryServiceAdapter {

    @QueryHandler
    public TestDomainDTO handle(GetTestDomainQuery query) {
        NameNameQueryParam<TestDomainDTO> nameQueryParam = new NameNameQueryParam<>("TestDomain.get", TestDomainDTO.class);
        return this.get(nameQueryParam).orElseThrow();
    }
}
