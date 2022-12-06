package core.framework.test.hibernate.query;

import core.framework.query.QueryCommand;

/**
 * @author ebin
 */
public class GetTestDomainCommand extends QueryCommand<TestDomainDTO> {
    public GetTestDomainCommand() {
        super("TestDomain.get", TestDomainDTO.class);
    }
}
