# Solr container

This is a container based on CentOS 7 which will set up and run a Solr instance suitable for using with the LOCKSS
Solr indexer. This can be built with 
`docker build -t lockss/solr .` 
and run with 
`docker run -d --name solr -p 8983:8983 lockss/solr` 
