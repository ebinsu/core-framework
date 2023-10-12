package core.framework.namequery.support.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
public class DefaultPlaceholderTokenHandler implements TokenHandler {
    private final List<String> parameterNames = new ArrayList<>();

    public List<String> getParameterNames() {
        return parameterNames;
    }

    @Override
    public String handleToken(String content) {
        parameterNames.add(content);
        return "?";
    }
}
