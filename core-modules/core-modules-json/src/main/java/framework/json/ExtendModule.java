package framework.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import framework.json.serializer.ObjectIdJsonSerializer;
import org.bson.types.ObjectId;

/**
 * @author ebin
 */
public class ExtendModule extends SimpleModule {
    public ExtendModule() {
        addSerializer(ObjectId.class, new ObjectIdJsonSerializer());
    }
}
