package core.framework.namedquery;

import java.util.List;

/**
 * @author ebin
 */
public record PagingResult<T>(Long total, List<T> data) {
}
