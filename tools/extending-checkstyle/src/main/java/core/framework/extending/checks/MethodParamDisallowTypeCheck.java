package core.framework.extending.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author ebin
 */
public class MethodParamDisallowTypeCheck extends AbstractCheck {
    private final List<String> disallowClasses = new ArrayList<>();
    private Pattern path = Pattern.compile(".*");
    private boolean processCurrentFile;
    private final List<String> matchDisallowClass = new ArrayList<>();

    public final void setPath(String from) {
        path = CommonUtil.createPattern(from);
    }

    public final void setDisallowClasses(String... from) {
        disallowClasses.clear();
        Collections.addAll(disallowClasses, from);
    }

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[]{
                TokenTypes.TYPE
        };
    }

    @Override
    public int[] getRequiredTokens() {
        return CommonUtil.EMPTY_INT_ARRAY;
    }

    @Override
    public void beginTree(DetailAST root) {
        matchDisallowClass.clear();
        processCurrentFile = path.matcher(getFilePath()).find();
        if (processCurrentFile) {
            for (DetailAST ast = root.getFirstChild(); ast != null; ast = ast.getNextSibling()) {
                if (TokenTypes.IMPORT == ast.getType()) {
                    DetailAST startingDot = ast.getFirstChild();
                    FullIdent name = FullIdent.createFullIdent(startingDot);
                    String importText = name.getText();

                    for (String disallowClass : disallowClasses) {
                        if (disallowClass.contains(importText)) {
                            matchDisallowClass.add(importText);
                        }
                    }
                }
            }
            processCurrentFile = !matchDisallowClass.isEmpty();
        }
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (processCurrentFile && ast.getParent() != null && TokenTypes.PARAMETER_DEF == ast.getParent().getType()) {
            // PARAMETER_DEF -> PARAMETERS -> METHOD_DEF
            DetailAST maybeParameters = ast.getParent().getParent();
            if (maybeParameters != null && TokenTypes.PARAMETERS == maybeParameters.getType()) {
                DetailAST maybeMethodDef = maybeParameters.getParent();
                if (maybeMethodDef != null && TokenTypes.METHOD_DEF == maybeMethodDef.getType()) {
                    matchDisallowClass.forEach(pkg -> {
                        if (pkg.contains(ast.getFirstChild().getText())) {
                            log(ast.getLineNo(), ast.getColumnNo(), "method.param.disallow.type");
                        }
                    });
                }
            }
        }
    }
}
