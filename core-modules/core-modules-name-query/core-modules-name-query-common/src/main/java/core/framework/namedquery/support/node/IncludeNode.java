package core.framework.namedquery.support.node;

import core.framework.namedquery.support.NamedQueryContext;
import core.framework.namedquery.support.parser.XMLNode;

import java.util.Optional;

/**
 * @author ebin
 */
public class IncludeNode implements Node {
    private final String refId;

    public IncludeNode(String refId) {
        this.refId = refId;
    }

    @Override
    public boolean apply(NamedQueryContext context) {
        String id = refId;
        if (!refId.contains(FragmentNode.ID_SEPARATOR)) {
            id = context.getNamespace() + FragmentNode.ID_SEPARATOR + refId;
        }
        Optional<FragmentNode> fragmentNodeOptional = context.getFragmentNode(id);
        if (fragmentNodeOptional.isPresent()) {
            FragmentNode fragmentNode = fragmentNodeOptional.get();
            fragmentNode.apply(context);
        }
        return true;
    }

    public static class Builder implements NodeBuilder {
        @Override
        public Node build(String namespace, XMLNode nodeToHandle) {
            String refId = nodeToHandle.getAttributes().getProperty("ref-id");
            return new IncludeNode(refId);
        }
    }
}
