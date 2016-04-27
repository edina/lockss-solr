package org.lockss;

import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jwat.common.*;
import org.jwat.warc.WarcRecord;
import org.slf4j.*;

import java.io.*;
import java.util.*;

public class SolrIndexer {

    private static Logger logger = LoggerFactory.getLogger(SolrIndexer.class);

    private WarcRecord record;
    private Payload payload;
    private SolrInputDocument solrDoc;
    private Map<String, String> httpHeaders;

    private int indexed = 0;

    /**
     * Takes the WARC record and works out what to do with it. If the Content-Type is text/html or text/plain then it
     * gets indexed, otherwise it is ignored.
     * TODO: Decide what other content types should be indexed. PDF, Word document?
     * @param record The WARC record that is to be analysed and indexed
     */
    public void index(WarcRecord record) {
        this.record = record;
        this.payload = record.getPayload();
        solrDoc = new SolrInputDocument();
        httpHeaders = new HashMap<>();
        getHeaders();
        // The Content-Type header can have all sorts of information about the character set, etc. This needs to be
        // stripped so that the string matching works. An alternative is using regex but that is liable to get ugly.
        switch (httpHeaders.get("Content-Type").split(";")[0]) {
            case "text/html":
            case "text/plain":
                indexWARC();
                indexHeaders();
                indexHTML();
                SolrHandler.solrAdd(solrDoc);
                ++indexed;
        }
    }

    /**
     * Extracts the HTTP headers from the payload and put them into a hash so that they can be accessed sensibly
     */
    private void getHeaders() {
        PayloadWithHeaderAbstract headerAbstract = payload.getPayloadHeaderWrapped();
        List<HeaderLine> headerLineList = headerAbstract.getHeaderList();
        for (HeaderLine headerline : headerLineList) {
            httpHeaders.put(headerline.name, headerline.value);
        }
    }

    /**
     * Adds the WARC headers to the Solr document
     */
    private void indexWARC() {
        solrDoc.addField("response_url", record.getHeader("WARC-Target-URI").value);
        solrDoc.addField("response_dt", record.getHeader("WARC-Date").value);
    }

    /**
     * Adds the HTTP headers to the Solr document
     */
    private void indexHeaders() {
        logger.info("Content-type:" + httpHeaders.get("Content-Type"));
        solrDoc.addField("contentType_s", httpHeaders.get("Content-Type"));
    }

    /**
     * Extracts important elements from the HTML and indexes those. Also takes all of the text in the HTML and indexes
     * that in the body_t field so that full-text searching can be done
     * TODO: What other elements should be indexed separately? meta tags, header tags?
     */
    private void indexHTML() {
        InputStream is = record.getPayloadContent();
        String html = getStringFromInputStream(is);
        Document doc = Jsoup.parse(html);
        Elements bodyElements = doc.getElementsByTag("html");
        Element bodyElement = bodyElements.get(0);
        String bodyString = bodyElement.text();
        solrDoc.addField("body_t", bodyString);
        Elements titles = doc.getElementsByTag("title");
        for (Element title : titles) {
            solrDoc.addField("title_t", title.text());
        }
    }

    /**
     * Converts an InputStream object into a String
     * @param is InputStream to be converted
     * @return String constructed from the InputStream
     */
    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
        } catch (IOException e) {
            logger.error("Could not read stream", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error("Could not close reader", e);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Accessor for the number of records indexed
     * @return int respresenting the number of records indexed
     */
    public int getIndexedCount() {
        return indexed;
    }
}
