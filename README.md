# lockss-solr

This is an application that takes WARC files and indexes them in Solr. A ready-to-use Solr Docker configuration can be found in `solr/`

The application is built using gradle. To run the app use the command `gradle runApp` which should pull the depedencies, compile the code and run it.
A jar can be created by running `gradle fatJar` and the jar file can be found in `build/libs/`.

The application can be built and run using the Dockerfile. The watch directory containing the WARC files should be mounted so that the application can 
access thee files. The application is configured by using environment variables, these are LOCKSS_SOLR_WATCHDIR, LOCKSS_SOLR_URL and LOCKSS_SOLR_BATCH_SIZE.
Default values are provided by the application but these can be overridden when necessary. An example Docker command to start the application is given below.

`docker run -it --rm -e LOCKSS_SOLR_WATCHDIR=/samples -e LOCKSS_SOLR_URL=http://192.168.56.103:8983/solr/test-core -v /home/rwincewicz/workspace/lockss/lockss-solr/samples:/samples:ro lockss/indexer`
