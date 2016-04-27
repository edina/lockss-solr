package org.lockss;

import org.jwat.warc.*;
import org.slf4j.*;

import java.io.*;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class WarcWatcher {

    private static Logger logger = LoggerFactory.getLogger(WarcWatcher.class);
    private int records = 0;
    private int processed = 0;

    private SolrIndexer solrIndexer = new SolrIndexer();

    /**
     * Creates a directory watcher based on the path variable that is passed to it. Any new file in this directory will
     * be processed. A check is done to ensure that it is a file before continuing.
     * @param path The path of the directory that should be watched for changes
     */
    WarcWatcher(Path path) {
        WatchService watchService = null;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException ioe) {
            logger.error("Could not start watcher", ioe);
        }
        for (;;) {
            WatchKey key;
            try {
                assert watchService != null;
                key = watchService.take();
            } catch (InterruptedException ie) {
                logger.error("Could not get watched file", ie);
                return;
            }
            long startTime = System.nanoTime();
            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();
                // The ev.context() method just returns the filename rather than the full path. If the File object is
                // created from the Path object then the root path of the application is prepended. This results in an
                // incorrect path so the watch directory is prepended instead, resulting in the correct path.
                File file = new File(path.toString() + "/" + filename.toString());
                logger.info(file.getAbsolutePath());
                if (file.isFile()) {
                    processFiles(file);
                } else {
                    logger.info("Not a file");
                }
            }
            // When the file has been processed update the Solr index with any documents that may be left in the list
            // of documents
            SolrHandler.solrCommit();
            long stopTime = System.nanoTime();
            // Log how long it takes to process a file. Useful to compare document update batch sizes
            logger.info("File took " + (stopTime - startTime)/1000000 + " ms to process");
            printReport();
            // Important that the key is reset so that it can continue to watch for new files.
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    /**
     * Checks the file and determines what to do with it. If it's a .warc file then it is sent for parsing. If it's a
     * .warc.gz then it's uncompressed and then sent for parsing. If it's neither then it is ignored.
     * @param file The file to be parsed and indexed
     */
    private void processFiles(File file) {
        // If only java dealt with regex better. This is ugly but seems to be the best way of handling this at the
        // moment
        Pattern gzipPattern = Pattern.compile(".*\\.warc\\.gz$");
        Matcher gzipM = gzipPattern.matcher(file.getName());
        Pattern warcPattern = Pattern.compile(".*\\.warc$");
        Matcher warcM = warcPattern.matcher(file.getName());
        if (gzipM.matches()) {
            logger.info("Matched warc.gz");
            unzipWarcFile(file);
        } else if (warcM.matches()) {
            logger.info("Matched warc");
            try {
                readWarcFile(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                logger.error("Could not find file", e);
            }
        } else {
            logger.info("File didn't match: " + file.getName());
        }
    }

    /**
     * Unzips the compressed file and then passes it on to the WARC reader
     * @param file File to be uncompressed
     */
    private void unzipWarcFile(File file) {
        InputStream in = null;
        try {
            in = new GZIPInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.error("Could not find file", e);
        } catch (IOException e) {
            logger.error("Could not read file", e);
        }
        readWarcFile(in);
    }

    /**
     * Parses the WARC file and sends each of the records off to be analysed and indexed by Solr
     * @param in The InputStream object either from the .warc file or the uncompressed .warc.gz file
     */
    private void readWarcFile(InputStream in) {
        try {
            WarcReader reader = WarcReaderFactory.getReader(in);
            WarcRecord record;
            while ((record = reader.getNextRecord()) != null) {
                if ("response".equals(record.getHeader("WARC-Type").value)) {
                    solrIndexer.index(record);
                    ++processed;
                }
                ++records;
            }
            // Ensure the reader and stream are closed
            reader.close();
            in.close();
        } catch (FileNotFoundException e) {
            logger.error("Could not find file", e);
        } catch (IOException e) {
            logger.error("Could not read file", e);
        }
    }

    /**
     * Prints a report of the parsing and indexing once the file has been processed
     */
    private void printReport() {
        logger.info("--------------");
        logger.info("       Records: " + records);
        logger.info("       Processed: " + processed);
        logger.info("       Indexed: " + solrIndexer.getIndexedCount());
    }
}
