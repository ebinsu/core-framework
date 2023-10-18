package core.framework.namedquery.support.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public class IndexPlaceholderTokenHandler implements TokenHandler {
    private final List<String> parameterNames = new ArrayList<>();

    public List<String> getParameterNames() {
        return parameterNames;
    }

    @Override
    public String handleToken(String content) {
        int index;
        Optional<String> first = parameterNames.stream().filter(f -> f.equals(content)).findFirst();
        if (first.isPresent()) {
            index = parameterNames.indexOf(first.get());
        } else {
            index = parameterNames.size();
            parameterNames.add(content);
        }
        return "?" + index;
    }
}
