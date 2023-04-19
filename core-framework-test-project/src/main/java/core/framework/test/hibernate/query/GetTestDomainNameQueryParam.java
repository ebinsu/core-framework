package core.framework.test.hibernate.query;

import core.framework.query.support.namequery.NameQueryParamImpl;

/**
 * @author ebin
 */
public class GetTestDomainNameQueryParam extends NameQueryParamImpl<TestDomainDTO> {
    public GetTestDomainNameQueryParam() {
        super("TestDomain.get", TestDomainDTO.class);
    }
}
