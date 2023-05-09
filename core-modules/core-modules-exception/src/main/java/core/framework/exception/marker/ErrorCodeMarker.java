package core.framework.exception.marker;

import java.io.Serial;

/**
 * @author ebin
 */
public class ErrorCodeMarker extends AbstractMarker {
    @Serial
    private static final long serialVersionUID = -7160891028896920752L;

    private final String code;

    public ErrorCodeMarker(String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return code;
    }
}
