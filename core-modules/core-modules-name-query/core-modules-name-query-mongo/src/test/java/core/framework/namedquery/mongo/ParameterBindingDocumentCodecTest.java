package core.framework.namedquery.mongo;

import org.bson.Document;
import org.springframework.data.mongodb.util.json.ParameterBindingDocumentCodec;

/**
 * @author ebin
 */
public class ParameterBindingDocumentCodecTest {
    public static void main(String[] args) {
        ParameterBindingDocumentCodec parameterBindingDocumentCodec = new ParameterBindingDocumentCodec();
        Document decode = parameterBindingDocumentCodec.decode("{ name: ?0}", new Object[]{"xx"});
        System.out.println(decode);
    }
}
