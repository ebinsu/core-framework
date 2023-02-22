package core.framework.extending.checks;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
public class DomainLayerCheckTest {
    @Test
    public void test() throws Exception {
        Checker checker = prepareCheckStyleChecker();
        List<File> files = prepareFilesToBeChecked();
        int numberOfErrors = checker.process(files);
        System.out.println(numberOfErrors);
    }

    private Checker prepareCheckStyleChecker() throws CheckstyleException {
        Checker checker = new Checker();
        checker.setModuleClassLoader(Thread.currentThread().getContextClassLoader());
        checker.configure(prepareConfiguration());
        return checker;
    }

    private DefaultConfiguration prepareConfiguration() {
        DefaultConfiguration checks = new DefaultConfiguration("Checks");
        DefaultConfiguration treeWalker = new DefaultConfiguration("TreeWalker");
        DefaultConfiguration check = new DefaultConfiguration(DomainLayerCheck.class.getCanonicalName());
        checks.addChild(treeWalker);
        treeWalker.addChild(check);
        return checks;
    }

    private List<File> prepareFilesToBeChecked() {
        File testFile1 = new File(getClass().getClassLoader().getResource("domain/Demo.java").getFile());
        File testFile2 = new File(getClass().getClassLoader().getResource("domain/TestDomain.java").getFile());
        List<File> files = new ArrayList<File>();
        files.add(testFile1);
        files.add(testFile2);
        return files;
    }
}
