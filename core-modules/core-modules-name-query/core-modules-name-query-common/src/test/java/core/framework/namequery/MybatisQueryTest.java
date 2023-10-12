package core.framework.namequery;

import core.framework.namequery.support.NamedQueryContext;
import core.framework.namequery.support.node.MixedNode;
import core.framework.namequery.support.parser.NamedQueryXMLParser;
import org.apache.ibatis.io.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
public class MybatisQueryTest {

    @Test
    public void test_resource() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("core/framework/namequery/support/namequery.dtd");
        System.out.println(resourceAsStream);
        System.out.println(new ClassPathResource("core/framework/namequery/support/namequery.dtd").exists());
    }

    @Test
    public void test_dtd() throws IOException {
        NamedQueryXMLParser xPathParser1 = new NamedQueryXMLParser();

        List<MixedNode> nodes = xPathParser1.parse(List.of("TestQuery.xml"));
        NamedQueryContext context = new NamedQueryContext(Map.of("name", "NAME_VARIABLE"));
        nodes.get(0).apply(context);
        System.out.println(context.getQuery());
    }


}
