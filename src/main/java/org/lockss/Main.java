package org.lockss;

import org.slf4j.*;

import java.nio.file.*;
import java.util.Map;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        IndexerConfig indexerConfig = new IndexerConfig();
        Map<String, String> config = indexerConfig.getConfig();
        String watchPath = config.get(IndexerConfig.LOCKSS_SOLR_WATCHDIR);
        logger.info("Watch folder: " + watchPath);
        Path path = FileSystems.getDefault().getPath(watchPath);
        new WarcWatcher(path);
    }
}
