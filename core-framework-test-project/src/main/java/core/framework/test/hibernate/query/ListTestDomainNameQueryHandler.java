package core.framework.test.hibernate.query;

import core.framework.query.annotation.QueryHandler;
import core.framework.query.support.namequery.NameNameQueryParam;
import core.framework.query.support.namequery.NameNameQueryServiceAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ebin
 */
@Service
public class ListTestDomainNameQueryHandler extends NameNameQueryServiceAdapter {
    @Transactional
    @QueryHandler
    public List<TestDomainDTO> handle(ListTestDomainQuery query) {
        NameNameQueryParam<TestDomainDTO> nameQueryParam = new NameNameQueryParam<>("TestDomain.get", TestDomainDTO.class);
        return this.select(nameQueryParam);
    }
}
