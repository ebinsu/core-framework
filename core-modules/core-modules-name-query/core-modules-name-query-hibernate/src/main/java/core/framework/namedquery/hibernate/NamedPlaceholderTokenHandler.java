package core.framework.namedquery.hibernate;

import core.framework.namedquery.support.parser.TokenHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public class NamedPlaceholderTokenHandler implements TokenHandler {
    private final List<String> parameterNames = new ArrayList<>();

    public List<String> getParameterNames() {
        return parameterNames;
    }

    @Override
    public String handleToken(String content) {
        Optional<String> first = parameterNames.stream().filter(f -> f.equals(content)).findFirst();
        if (first.isEmpty()) {
            parameterNames.add(content);
        }
        return ":" + content;
    }
}
