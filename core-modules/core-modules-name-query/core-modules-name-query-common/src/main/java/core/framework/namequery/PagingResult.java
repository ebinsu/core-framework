package core.framework.namequery;

import java.util.Collections;
import java.util.List;

/**
 * @author ebin
 */
public record PagingResult<T>(Long total, List<T> data) {
    public PagingResult(Long total, List<T> data) {
        this.total = total;
        this.data = Collections.unmodifiableList(data);
    }
}
