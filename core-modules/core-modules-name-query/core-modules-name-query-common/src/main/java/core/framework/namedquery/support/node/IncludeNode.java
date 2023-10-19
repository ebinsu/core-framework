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
        Optional<FragmentNode> fragmentNodeOptional = context.getFragmentNode(refId);
        if (fragmentNodeOptional.isPresent()) {
            FragmentNode fragmentNode = fragmentNodeOptional.get();
            fragmentNode.apply(context);
        }
        return true;
    }

    public static class Builder implements NodeBuilder {
        @Override
        public Node build(XMLNode nodeToHandle) {
            String refId = nodeToHandle.getAttributes().getProperty("ref-id");
            return new IncludeNode(refId);
        }
    }
}
