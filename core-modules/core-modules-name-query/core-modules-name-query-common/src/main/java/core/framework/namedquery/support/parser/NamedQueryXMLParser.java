package core.framework.namedquery.support.parser;

import core.framework.namedquery.support.ResolverContext;
import core.framework.namedquery.support.node.FragmentNode;
import core.framework.namedquery.support.node.MixedNode;
import core.framework.namedquery.support.node.NodeBuilder;
import core.framework.namedquery.support.node.NodeBuilderProvider;
import core.framework.shared.utils.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author ebin
 */
public class NamedQueryXMLParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(NamedQueryXMLParser.class);

    public Pair<List<MixedNode>, List<FragmentNode>> parse(Collection<NodeBuilderProvider> nodeBuilderProviders,
                                                           List<Resource> resources) {
        StopWatch stopWatch = new StopWatch();
        ResolverContext resolverContext = new ResolverContext();
        nodeBuilderProviders.forEach(provider -> provider.get().forEach(resolverContext::register));

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
                    String name = child.getName();
                    NodeBuilder nodeBuilder = resolverContext.getNodeBuilder(name);
                    core.framework.namedquery.support.node.Node node = nodeBuilder.build(namespace, child);
                    if (node instanceof MixedNode mixedNode) {
                        nodes.add(mixedNode);
                    } else if (node instanceof FragmentNode fragmentNode) {
                        fragmentNodes.add(fragmentNode);
                    }
                }
            }
        }
        Pair<List<MixedNode>, List<FragmentNode>> result = Pair.of(nodes, fragmentNodes);
        long elapsed = stopWatch.elapsed();
        LOGGER.info(" Finished parse named query in {} ms.", Duration.ofNanos(elapsed));
        return result;
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
            throw new RuntimeException("Error evaluating XPath.  Cause: " + e, e);
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
            throw new RuntimeException("Error creating document instance.  Cause: " + e, e);
        }
    }
}
