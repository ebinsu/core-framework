package test1.domain;

import test2.domain.domainLayerClassC;
import core.framework.ddd.annotation.AggregateRoot;

/**
 * @author ebin
 */
@AggregateRoot
public class DomainLayerClassA {
    public String id;
    public DomainLayerClassB domainLayerClassB;
    public DomainLayerClassC domainLayerClassC;
    String sql1 = "insert * from users";

    public void MethodA() {
        String sql2 = "update " +
                "users set a = a" +
                " b = b where 1=1";

        String sql3 = """
                delete 
                * 
                from 
                user
                where id = 1
                """;

    }
}
