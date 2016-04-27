package org.lockss;

import org.slf4j.*;

import java.nio.file.*;
import java.util.Properties;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        IndexerConfig config = new IndexerConfig();
        Properties properties = config.getConfig();
        String watchPath = properties.getProperty("watchPath");
        logger.info("Watch folder: " + watchPath);
        Path path = FileSystems.getDefault().getPath(watchPath);
        new WarcWatcher(path);
    }
}
