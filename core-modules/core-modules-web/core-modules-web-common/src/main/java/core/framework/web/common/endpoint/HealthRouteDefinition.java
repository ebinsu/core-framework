package core.framework.web.common.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author ebin
 */
public class HealthRouteDefinition extends AbstractRouteDefinition {
    public HealthRouteDefinition() {
        super("/health", RequestMethod.GET);
    }

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok();
    }
}
