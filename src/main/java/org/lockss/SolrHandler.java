package org.lockss;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SolrHandler {

    private static Logger logger = LoggerFactory.getLogger(SolrHandler.class);

    private static SolrClient client = null;

    private static List<SolrInputDocument> documents = new ArrayList<>();

    /**
     * Returns the single SolrClient object if it exists and creates it if it doesn't
     * @return SolrClient object used to connect to the Solr server
     */
    public static SolrClient getClient() {
        IndexerConfig config = new IndexerConfig();
        Properties properties = config.getConfig();
        String solrUrl = properties.getProperty("solrURL");
        if (client == null) {
            logger.info("Connecting to Solr server at: " + solrUrl);
            client = new HttpSolrClient(solrUrl);
        }
        return client;
    }

    /**
     * Adds the Solr Document to a list and, if the list is long enough, updates the Solr index with the documents
     * @param doc Solr document to be added to the list
     */
    public static void solrAdd(SolrInputDocument doc) {
        documents.add(doc);
        if (documents.size() > 10) {
            solrCommit();
        }
    }

    /**
     * Updates the Solr index with the list of Solr documents
     */
    public static void solrCommit() {
        logger.info("Updating Solr server");
        if (documents.size() > 0) {
            try {
                SolrClient client = SolrHandler.getClient();
                client.add(documents);
                client.commit();
                documents.clear();
            } catch (SolrServerException | IOException e) {
                logger.error("Could not update Solr index", e);
            }
        }
    }
}