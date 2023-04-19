package core.framework.test.hibernate.query;

import core.framework.query.support.namequery.NameNameQueryParam;

/**
 * @author ebin
 */
public class GetTestDomainNameQueryParam extends NameNameQueryParam<TestDomainDTO> {
    public GetTestDomainNameQueryParam() {
        super("TestDomain.get", TestDomainDTO.class);
    }
}
