package core.framework.namedquery.support;

import core.framework.namedquery.support.node.FragmentNode;
import core.framework.namedquery.support.node.MixedNode;
import core.framework.namedquery.support.parser.NamedQueryXMLParser;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author ebin
 */
public class NamedQueryResourceHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(NamedQueryResourceHolder.class);
    private final String resourceDir;
    private final String fileName;
    private final ResolverContext resolverContext;
    private final Resource resource;
    private List<MixedNode> mixedNodes;
    private List<FragmentNode> fragmentNodes;
    private long lastModified;
    private volatile boolean refreshing;

    public NamedQueryResourceHolder(ResolverContext resolverContext, Resource resource) {
        this.resolverContext = resolverContext;
        this.resource = resource;
        Pair<List<MixedNode>, List<FragmentNode>> parsed =
            NamedQueryXMLParser.parse(resolverContext, resource);
        this.mixedNodes = parsed.getLeft();
        this.fragmentNodes = parsed.getRight();
        try {
            File file = resource.getFile();
            this.lastModified = file.lastModified();
            this.resourceDir = file.getParent();
            this.fileName = resource.getFilename();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void refresh() {
        if (!refreshing) {
            refreshing = true;
            Pair<List<MixedNode>, List<FragmentNode>> parsed;
            try {
                parsed = NamedQueryXMLParser.parse(resolverContext, resource);
            } catch (Exception e) {
                parsed = Pair.of(List.of(), List.of());
                LOGGER.warn("Named query refresh failed", e);
            } finally {
                refreshing = false;
            }
            try {
                File file = resource.getFile();
                this.lastModified = file.lastModified();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.mixedNodes = parsed.getLeft();
            this.fragmentNodes = parsed.getRight();
        }
    }

    public String getResourceDir() {
        return resourceDir;
    }

    public String getFileName() {
        return fileName;
    }

    public File getFile() {
        try {
            return this.resource.getFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isModified(File file) {
        return file.lastModified() != this.lastModified && file.length() > 0;
    }

    public List<MixedNode> getMixedNodes() {
        return Collections.unmodifiableList(mixedNodes);
    }

    public List<FragmentNode> getFragmentNodes() {
        return Collections.unmodifiableList(fragmentNodes);
    }
}
