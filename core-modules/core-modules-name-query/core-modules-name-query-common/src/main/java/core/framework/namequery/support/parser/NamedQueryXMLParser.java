package core.framework.namequery.support.parser;

import core.framework.namequery.QueryType;
import core.framework.namequery.support.ResolverContext;
import core.framework.namequery.support.node.MixedNode;
import core.framework.namequery.support.node.MongoNode;
import core.framework.namequery.support.node.SqlNode;
import org.apache.ibatis.builder.BuilderException;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public class NamedQueryXMLParser {
    public List<MixedNode> parse(List<String> paths) {
        ResolverContext resolverContext = new ResolverContext();
        List<MixedNode> nodes = new ArrayList<>(paths.size());
        for (String path : paths) {
            Document document;
            ClassPathResource classPathResource = new ClassPathResource(path);
            try (InputStream inputStream = classPathResource.getInputStream()) {
                document = createDocument(new InputSource(inputStream));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            XMLNode namedQueryXMLNode = evalNode(document, "/named-queries", resolverContext);
            if (namedQueryXMLNode != null) {
                for (XMLNode child : namedQueryXMLNode.getChildren()) {
                    MixedNode node;
                    String name = child.getName();
                    Class<?> resultClass;
                    try {
                        resultClass = Class.forName(child.getAttributes().getProperty("result-class"));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    if ("sql".equals(name)) {
                        node = new SqlNode(
                            child.getAttributes().getProperty("id"),
                            QueryType.of(child.getName()),
                            resultClass,
                            ChildrenNodeHelper.build(child)
                        );
                    } else {
                        MongoNode.ReadPreference readPreference = Optional.ofNullable(child.getAttributes().getProperty("read-preference")).map(MongoNode.ReadPreference::valueOf).orElse(null);
                        node = new MongoNode(
                            child.getAttributes().getProperty("id"),
                            QueryType.of(child.getName()),
                            resultClass,
                            ChildrenNodeHelper.build(child),
                            readPreference
                        );
                    }

                    nodes.add(node);
                }
            }
        }
        return nodes;
    }

    public XMLNode evalNode(Object root, String expression, ResolverContext resolverContext) {
        Node node = (Node) evaluate(expression, root, resolverContext);
        if (node == null) {
            return null;
        }
        return new XMLNode(resolverContext, node);
    }

    private Object evaluate(String expression, Object root, ResolverContext resolverContext) {
        try {
            return resolverContext.getXpath().evaluate(expression, root, XPathConstants.NODE);
        } catch (Exception e) {
            throw new BuilderException("Error evaluating XPath.  Cause: " + e, e);
        }
    }

    private Document createDocument(InputSource inputSource) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setValidating(false);

            factory.setNamespaceAware(false);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(false);
            factory.setCoalescing(false);
            factory.setExpandEntityReferences(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new NamedQueryEntityResolver());
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    // NOP
                }
            });
            return builder.parse(inputSource);
        } catch (Exception e) {
            throw new BuilderException("Error creating document instance.  Cause: " + e, e);
        }
    }
}
