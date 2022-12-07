package core.framework.test.hibernate.query;

import core.framework.query.support.namequery.NameQueryParam;

/**
 * @author ebin
 */
public class GetTestDomainQueryParam extends NameQueryParam<TestDomainDTO> {
    public GetTestDomainQueryParam() {
        super("TestDomain.get", TestDomainDTO.class);
    }
}
