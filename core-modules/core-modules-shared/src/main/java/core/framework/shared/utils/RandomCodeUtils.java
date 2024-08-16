package core.framework.shared.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author ebin
 */
public final class RandomCodeUtils {
    private RandomCodeUtils() {
    }

    public static String next() {
        return StringUtils.leftPad(
            String.valueOf(
                ThreadLocalRandom.current().nextInt(999999)
            ),
            6,
            "0"
        );
    }
}
