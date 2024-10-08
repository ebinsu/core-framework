package core.framework.bson.converter;

import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
public final class ExtendConverters {
    public static List<Converter<?, ?>> getConvertersToRegister() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(DateToZoneDateTimeConverter.INSTANCE);
        return converters;
    }
}
