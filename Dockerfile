FROM centos:centos7

RUN yum install -y java-1.8.0-openjdk-devel unzip

ADD https://services.gradle.org/distributions/gradle-2.13-all.zip ./gradle-2.13-all.zip
RUN mkdir -p /opt/gradle
RUN unzip ./gradle-2.13-all.zip -d /opt/gradle

ENV GRADLE_HOME /opt/gradle/gradle-2.13

ENV PATH $PATH:/opt/gradle/gradle-2.13/bin:/usr/lib/jvm/jre/bin

ENV JAVA_HOME /usr/lib/jvm/jre

RUN mkdir /lockss-solr

ADD ./src /lockss-solr/src
ADD ./*.gradle /lockss-solr/
ADD ./gradle /lockss-solr/

WORKDIR /lockss-solr

RUN gradle fatJar

USER nobody

CMD ["java", "-jar", "/lockss-solr/build/libs/lockss-solr-all-1.0-SNAPSHOT.jar"]
