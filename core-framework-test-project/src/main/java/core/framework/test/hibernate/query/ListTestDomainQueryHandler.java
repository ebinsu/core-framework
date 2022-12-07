package core.framework.test.hibernate.query;

import core.framework.query.QueryHandler;
import core.framework.query.support.namequery.NameQueryParam;
import core.framework.query.support.namequery.NameQueryServiceAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ebin
 */
@Service
public class ListTestDomainQueryHandler extends NameQueryServiceAdapter implements QueryHandler<ListTestDomainQuery, List<TestDomainDTO>> {
    @Transactional
    @Override
    public List<TestDomainDTO> handle(ListTestDomainQuery query) {
        NameQueryParam<TestDomainDTO> nameQueryParam = new NameQueryParam<>("TestDomain.get", TestDomainDTO.class);
        return this.select(nameQueryParam);
    }
}
