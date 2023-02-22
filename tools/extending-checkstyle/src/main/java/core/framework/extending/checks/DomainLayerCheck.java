package core.framework.extending.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ebin
 */
public class DomainLayerCheck extends AbstractCheck {
    private final Pattern disAllowAnotherLayerPkgs = Pattern.compile(".*\\.(infrastructure|interfaces|application).*");
    private final Pattern domainBasePkgs = Pattern.compile(".*\\.domain");
    private final Pattern domainBaseDirPath = Pattern.compile("^.*[\\\\/]domain\\.?");
    private final Pattern domainBasePkgsPath = Pattern.compile(".*\\.domain");
    private final Set<String> disAllowPersistencePkgs = Set.of(
            "jakarta.persistence.EntityManager",
            "org.springframework.jdbc.core.JdbcTemplate",
            "javax.sql.DataSource"
    );

    private String currentDomainPackage;
    private boolean processCurrentFile;
    private Set<String> aggDomainPackages = new HashSet<>();

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[]{
                TokenTypes.IMPORT,
                TokenTypes.ANNOTATION
        };
    }

    @Override
    public int[] getRequiredTokens() {
        return CommonUtil.EMPTY_INT_ARRAY;
    }

    @Override
    public void beginTree(DetailAST root) {
        this.processCurrentFile = domainBasePkgsPath.matcher(getFilePath()).find() || domainBaseDirPath.matcher(getFilePath()).find();
        if (this.processCurrentFile) {
            this.currentDomainPackage = TokenUtil
                    .findFirstTokenByPredicate(root, r -> TokenTypes.PACKAGE_DEF == r.getType())
                    .map(pkgDef -> {
                        String fullPkg = FullIdent.createFullIdent(pkgDef.getFirstChild().getNextSibling()).getText();
                        Matcher matcher = domainBasePkgs.matcher(fullPkg);
                        if (matcher.find()) {
                            return matcher.group();
                        }
                        return fullPkg;
                    })
                    .orElse(null);
        }
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (this.processCurrentFile) {
            if (TokenTypes.IMPORT == ast.getType()) {
                DetailAST startingDot = ast.getFirstChild();
                FullIdent name = FullIdent.createFullIdent(startingDot);
                String importText = name.getText();
                if (disAllowAnotherLayerPkgs.matcher(importText).matches()) {
                    log(ast.getLineNo(), ast.getColumnNo(), "domain.layer.pkgs.disallow");
                } else if (disAllowPersistencePkgs.contains(importText)) {
                    log(ast.getLineNo(), ast.getColumnNo(), "domain.persistence.pkgs.disallow");
                } else if (currentDomainPackage != null
                        && domainBasePkgs.matcher(importText).find()
                        && !importText.contains(currentDomainPackage)) {
                    log(ast.getLineNo(), ast.getColumnNo(), "domain.entities.pkgs.disallow");
                }
            } else if (currentDomainPackage != null && TokenTypes.ANNOTATION == ast.getType()) {
                String annotationText = FullIdent.createFullIdent(ast.getLastChild()).getText();
                if ("AggregateRoot".equals(annotationText)) {
                    if (!aggDomainPackages.add(this.currentDomainPackage)) {
                        log(ast.getLineNo(), ast.getColumnNo(), "domain.multiple.agg.disallow");
                    }
                }
            }
        }
    }
}
