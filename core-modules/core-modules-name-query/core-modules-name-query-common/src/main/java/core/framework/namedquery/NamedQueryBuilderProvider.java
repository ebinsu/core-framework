package core.framework.namedquery;

import java.util.List;

/**
 * @author ebin
 */
public interface NamedQueryBuilderProvider {
    List<NamedQueryBuilder> get();
}
