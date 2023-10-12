package core.framework.test;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.util.NumberUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ebin
 */
public class StringTest {
    private static final Pattern SPEL_PARAMETER_BINDING_PATTERN = Pattern.compile("(\"\\?(\\d+)\"|'\\?(\\d+)'|\\?(\\d+))");
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy,M,dd");


    public static void main(String[] args) {
        System.out.println(ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
        String query = """
            {
                 find: "domain_event_tracking",
                 filter: {
                     '_id' : ?2
                 },
                 readConcern: { level: "majority" },
                 singleBatch: true,
                 batchSize: 64
             }
            """;
        Object[] param = {
            1,
            Date.from(LocalDate.of(2022, 9, 12).atTime(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant()),
            new ObjectId()
        };
        Matcher regexMatcher = SPEL_PARAMETER_BINDING_PATTERN.matcher(query);
        while (regexMatcher.find()) {
            String group = regexMatcher.group();
            int index = computeParameterIndex(group);
            System.out.println(index);
            Object value = param[index];
            query = query.replace(group, convertToQueryParamString(value));
            System.out.println(query);
        }
        Document parse = Document.parse(query);
        System.out.println(parse);
    }

    private static String convertToQueryParamString(Object value) {
        if (value instanceof ObjectId objectId) {
            return "new ObjectId('" + objectId + "')";
        } else if (value instanceof ZonedDateTime zonedDateTime) {
            return "ISODate(" + zonedDateTime.format(DateTimeFormatter.ISO_INSTANT) + ")";
        } else if (value instanceof LocalDateTime localDateTime) {
            return "ISODate(" + localDateTime.format(DateTimeFormatter.ISO_INSTANT) + ")";
        } else if (value instanceof LocalDate localDate) {
            return "new Date(" + localDate.getYear() + "," + localDate.getMonth() + "," + localDate.getDayOfMonth() + ")";
        } else if (value instanceof Date date) {
            return "new Date(" + simpleDateFormat.format(date) + ")";
        } else if (value instanceof Number) {
            return value.toString();
        }
        return "'" + value + "'";
    }

    private static int computeParameterIndex(String parameter) {
        return NumberUtils.parseNumber(parameter.replace("?", "").replace("'", "").replace("\"", ""), Integer.class);
    }
}
