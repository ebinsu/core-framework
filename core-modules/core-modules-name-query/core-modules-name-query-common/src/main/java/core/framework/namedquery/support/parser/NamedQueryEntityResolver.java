package core.framework.namedquery.support.parser;

import org.springframework.core.io.ClassPathResource;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * @author ebin
 */
public class NamedQueryEntityResolver implements EntityResolver {
    private static final String NAME_QUERY_DTD_PREFIX = "name-query";
    private static final String NAME_QUERY_DTD_FOLDER = "core/framework/namedquery/dtd/";

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        try {
            if (systemId != null) {
                String lowerCaseSystemId = systemId.toLowerCase(Locale.ENGLISH);
                if (lowerCaseSystemId.contains(NAME_QUERY_DTD_PREFIX)) {
                    return getInputSource(publicId, systemId);
                }
            }
            return null;
        } catch (Exception e) {
            throw new SAXException(e.toString());
        }
    }

    private InputSource getInputSource(String publicId, String systemId) {
        InputSource source = null;
        try {
            String dtdFileName = systemId.substring(systemId.lastIndexOf(NAME_QUERY_DTD_PREFIX));
            InputStream in = new ClassPathResource(NamedQueryEntityResolver.NAME_QUERY_DTD_FOLDER + dtdFileName).getInputStream();
            source = new InputSource(in);
            source.setPublicId(publicId);
            source.setSystemId(systemId);
        } catch (IOException e) {
            // ignore, null is ok
        }
        return source;
    }
}
