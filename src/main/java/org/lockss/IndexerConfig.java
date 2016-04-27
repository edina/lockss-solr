package org.lockss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IndexerConfig {

    private static Logger logger = LoggerFactory.getLogger(IndexerConfig.class);

    private static Properties properties = null;

    /**
     * Returns the Properties object and creates it if it doesn't exist
     * @return Properties object with the configuration loaded
     */
    public Properties getConfig() {
        if (properties == null) {
            loadConfig();
        }
        return properties;
    }

    /**
     * Pulls the configuration data from config.properties and populates the Properties object
     */
    private void loadConfig() {
        properties = new Properties();
        String propertiesFile = "config.properties";
        InputStream is = getClass().getClassLoader().getResourceAsStream(propertiesFile);
        if (is != null) {
            try {
                properties.load(is);
            } catch (IOException e) {
                logger.error("Could not read config file", e);
            }
        } else {
            logger.error("property file: '" + propertiesFile + "'could not be found");
        }
    }
}
