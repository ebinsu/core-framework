package core.framework.namedquery.impl;

import core.framework.namedquery.NamedQueryRepository;
import core.framework.namedquery.support.NamedQueryResourceHolder;
import core.framework.shared.utils.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public class NamedQueryResourceWatchService implements DisposableBean, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(NamedQueryResourceWatchService.class);
    private final NamedQueryRepository namedQueryRepository;
    private final WatchService watchService;
    private final Map<WatchKey, List<NamedQueryResourceHolder>> watchResources = new HashMap<>();
    private volatile boolean shutdown;

    public NamedQueryResourceWatchService(NamedQueryRepository repository,
                                          NamedQueryResourceFinder namedQueryResourceFinder) {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, List<NamedQueryResourceHolder>> groupByDir = namedQueryResourceFinder.getNamedQueryResourceHolders().stream().collect(Collectors.groupingBy(NamedQueryResourceHolder::getResourceDir));
        groupByDir.forEach((dir, v) -> {
            Path tempDir = Paths.get(dir);
            try {
                WatchKey key = tempDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                watchResources.put(key, v);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        this.namedQueryRepository = repository;
    }

    @Override
    public void destroy() throws Exception {
        this.shutdown = true;
    }

    private void watch() {
        while (!shutdown) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            List<NamedQueryResourceHolder> resourceHolders = watchResources.get(key);
            if (resourceHolders.isEmpty()) {
                continue;
            }
            List<WatchEvent<?>> keys = key.pollEvents();
            for (WatchEvent<?> watchEvent : keys) {
                WatchEvent.Kind<?> watchEventKind = watchEvent.kind();
                String fileName = watchEvent.context().toString();
                if (watchEventKind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    resourceHolders.stream().filter(f -> f.getFileName().equals(fileName)).findFirst().ifPresent(resourceHolder -> {
                        File file = resourceHolder.getFile();
                        // When you edit the content of a file through an editor,
                        // it'll modify both date (or other metadata) and content.
                        // WatcherServices reports events twice because the underlying file is updated twice.
                        // Once for the content and once for the file modified time.
                        if (resourceHolder.isModified(file)) {
                            StopWatch stopWatch = new StopWatch();
                            LOGGER.info("Name query {} will perform hot loading.", resourceHolder.getFileName());
                            resourceHolder.getMixedNodes().forEach(namedQueryRepository::remove);
                            resourceHolder.getFragmentNodes().forEach(namedQueryRepository::remove);
                            resourceHolder.refresh();
                            resourceHolder.getMixedNodes().forEach(namedQueryRepository::register);
                            resourceHolder.getFragmentNodes().forEach(namedQueryRepository::register);
                            long elapsed = stopWatch.elapsed();
                            LOGGER.info("Finished hot reload named query resource {} in {} ms.", resourceHolder.getFileName(), Duration.ofNanos(elapsed));
                        }
                    });
                }
                key.reset();
            }
        }
        try {
            this.watchService.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this::watch).start();
    }
}
