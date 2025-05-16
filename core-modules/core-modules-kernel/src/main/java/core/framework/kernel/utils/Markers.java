package core.framework.kernel.utils;

import core.framework.kernel.log.marker.ErrorCodeMarker;
import org.slf4j.Marker;

/**
 * @author ebin
 */
public final class Markers {
    public static Marker errorCode(String code) {
        return new ErrorCodeMarker(code);
    }
}