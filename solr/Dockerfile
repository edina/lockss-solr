FROM centos:centos7

RUN yum install -y java-1.8.0-openjdk tar

ADD http://mirror.ox.ac.uk/sites/rsync.apache.org/lucene/solr/6.0.0/solr-6.0.0.tgz /solr-6.0.0.tgz

RUN tar -zxf /solr-6.0.0.tgz

RUN mv /solr-6.0.0 /solr

ADD test-core /solr/server/solr/cores/test-core

CMD ["/solr/bin/solr", "-f"]
