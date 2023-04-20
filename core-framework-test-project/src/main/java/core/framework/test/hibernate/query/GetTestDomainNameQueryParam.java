package core.framework.test.hibernate.query;


import core.framework.namequery.impl.NameQueryParamImpl;

/**
 * @author ebin
 */
public class GetTestDomainNameQueryParam extends NameQueryParamImpl<TestDomainDTO> {
    public GetTestDomainNameQueryParam() {
        super("TestDomain.get", TestDomainDTO.class);
    }
}
