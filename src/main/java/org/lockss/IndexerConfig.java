package org.lockss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class IndexerConfig {

    private static Logger logger = LoggerFactory.getLogger(IndexerConfig.class);

    private static Map<String, String> env = null;

    public static String LOCKSS_SOLR_URL = "LOCKSS_SOLR_URL";
    public static String LOCKSS_SOLR_WATCHDIR = "LOCKSS_SOLR_WATCHDIR";
    public static String LOCKSS_SOLR_BATCH_SIZE = "LOCKSS_SOLR_BATCH_SIZE";

    private static String LOCKSS_SOLR_URL_VALUE = "http://localhost:8983/solr/test-core";
    private static String LOCKSS_SOLR_WATCHDIR_VALUE = "samples/";
    private static String LOCKSS_SOLR_BATCH_SIZE_VALUE = "10";

    /**
     * Returns the environment variables map and loads it if it doesn't exist
     * @return Map of configuration variables
     */
    public Map<String, String> getConfig() {
        if (env == null) {
            loadConfig();
        }
        return env;
    }

    /**
     * Loads a map of environment variables and sets some defaults if they don't exist.
     */
    public void loadConfig() {
        // The environment variable map is unmodifiable so we need to make a copy if we want to add these default values
        env = new HashMap<>(System.getenv());
        if (!env.containsKey(LOCKSS_SOLR_URL)) {
            logger.info("Solr URL not set so using default '" + LOCKSS_SOLR_URL_VALUE + "'");
            env.put(LOCKSS_SOLR_URL, LOCKSS_SOLR_URL_VALUE);
        }
        if (!env.containsKey(LOCKSS_SOLR_WATCHDIR)) {
            logger.info("Watch directory not set so using default '" + LOCKSS_SOLR_WATCHDIR_VALUE + "'");
            env.put(LOCKSS_SOLR_WATCHDIR, LOCKSS_SOLR_WATCHDIR_VALUE);
        }
        if (!env.containsKey(LOCKSS_SOLR_BATCH_SIZE)) {
            logger.info("Solr batch size not set so using default '" + LOCKSS_SOLR_BATCH_SIZE_VALUE + "'");
            env.put(LOCKSS_SOLR_BATCH_SIZE, LOCKSS_SOLR_BATCH_SIZE_VALUE);
        }
    }
}
