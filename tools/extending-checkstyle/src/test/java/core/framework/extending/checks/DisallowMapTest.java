package core.framework.extending.checks;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ebin
 */
public class DisallowMapTest {
    public static final String[] MAP_CLASS = new String[]{
            Map.class.getName(),
            AbstractMap.class.getName(),
            HashMap.class.getName(),
            EnumMap.class.getName(),
            LinkedHashMap.class.getName(),
            LinkedHashMap.class.getName(),
            ConcurrentMap.class.getName(),
            ConcurrentHashMap.class.getName()
    };

    @Test
    public void ignore_local_variables() throws Exception {
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
        DefaultConfiguration check = new DefaultConfiguration(MethodParamDisallowTypeCheck.class.getCanonicalName());
        check.addProperty("path", ".*Controller.*");
        check.addProperty("disallowClasses", Map.class.getName());
        checks.addChild(treeWalker);
        treeWalker.addChild(check);
        return checks;
    }

    private List<File> prepareFilesToBeChecked() {
        String testFileName = "TestClassWithErrorsController.java";
        URL testFileUrl = getClass().getClassLoader().getResource(testFileName);
        File testFile = new File(testFileUrl.getFile());
        List<File> files = new ArrayList<File>();
        files.add(testFile);
        return files;
    }
}
