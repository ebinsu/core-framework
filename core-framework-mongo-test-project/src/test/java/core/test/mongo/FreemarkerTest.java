package core.test.mongo;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public class FreemarkerTest {
    public static void main(String[] args) throws IOException {
        var cfg = new Configuration(new Version("2.3.31"));

        cfg.setClassForTemplateLoading(FreemarkerTest.class, "/views");
        cfg.setDefaultEncoding("UTF-8");

        Template test = new Template("test", "The message is: ${msg}", cfg);

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("msg", "Today is a beautiful day");

        try (StringWriter out = new StringWriter()) {

            test.process(templateData, out);
            System.out.println(out.getBuffer().toString());

            out.flush();
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }
}
