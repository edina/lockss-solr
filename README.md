# lockss-solr

This is an application that takes WARC files and indexes them in Solr. 

The application is built using gradle. To run the app use the command `gradle runApp` which should pull the depedencies, compile the code and run it.
A jar can be created by running `gradle fatJar` and the jar file can be found in `build/libs/`.

The configuration file allows the location of the watch directory and the URI where the Solr server can be found.
