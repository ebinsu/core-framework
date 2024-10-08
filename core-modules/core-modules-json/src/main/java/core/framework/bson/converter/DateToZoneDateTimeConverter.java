package core.framework.bson.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.Date;

import static java.time.ZoneId.systemDefault;

/**
 * @author ebin
 */
public enum DateToZoneDateTimeConverter implements Converter<Date, ZonedDateTime> {
    INSTANCE;

    @NonNull
    @Override
    public ZonedDateTime convert(Date source) {
        return ZonedDateTime.ofInstant(source.toInstant(), systemDefault());
    }
}
