package core.framework.namequery.support.parser;


/**
 * @author ebin
 */
public class DynamicCheckerTokenHandler implements TokenHandler {
    private boolean isDynamic;

    public boolean isDynamic() {
        return isDynamic;
    }

    @Override
    public String handleToken(String content) {
        this.isDynamic = true;
        return null;
    }
}
