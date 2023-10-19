package core.framework.namedquery.support.parser;

import core.framework.namedquery.support.ResolverContext;
import core.framework.namedquery.support.node.FragmentNode;
import core.framework.namedquery.support.node.MixedNode;
import core.framework.namedquery.support.node.mongo.MongoNode;
import core.framework.namedquery.support.node.sql.SqlNode;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.builder.BuilderException;
import org.springframework.core.io.Resource;
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

    public Pair<List<MixedNode>, List<FragmentNode>> parse(List<Resource> resources) {
        ResolverContext resolverContext = new ResolverContext();
        List<MixedNode> nodes = new ArrayList<>();
        List<FragmentNode> fragmentNodes = new ArrayList<>();
        for (Resource resource : resources) {
            Document document;
            try (InputStream inputStream = resource.getInputStream()) {
                document = createDocument(new InputSource(inputStream));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            XMLNode namedQueryXMLNode = evalNode(document, "/named-queries", resolverContext);
            if (namedQueryXMLNode != null) {
                String namespace = namedQueryXMLNode.getAttributes().getProperty("namespace");
                for (XMLNode child : namedQueryXMLNode.getChildren()) {
                    MixedNode node;
                    String name = child.getName();
                    Class<?> resultClass;
                    String id = child.getAttributes().getProperty("id");
                    if ("fragment".equals(name)) {
                        fragmentNodes.add(new FragmentNode(namespace, id, ChildrenNodeHelper.build(child)));
                        continue;
                    }
                    try {
                        resultClass = Class.forName(child.getAttributes().getProperty("result-class"));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    if ("sql".equals(name)) {
                        node = new SqlNode(namespace, id, resultClass, ChildrenNodeHelper.build(child));
                    } else {
                        MongoNode.ReadPreference readPreference = Optional.ofNullable(child.getAttributes().getProperty("read-preference"))
                            .map(MongoNode.ReadPreference::valueOf).orElse(MongoNode.ReadPreference.SECONDARY_PREFERRED);
                        node = new MongoNode(namespace, id, resultClass, ChildrenNodeHelper.build(child), readPreference);
                    }
                    nodes.add(node);
                }
            }
        }
        return Pair.of(nodes, fragmentNodes);
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
