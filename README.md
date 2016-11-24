[lockss-solr](https://github.com/edina/lockss-solr)
=======================

### Introduction
This is an application that takes WARC files in a given directory and indexes them in Solr. A ready-to-use Solr Docker configuration can be found in `solr/`

### Gradle build
The application is built using gradle. To run the app use the command `gradle runApp` which should pull the depedencies, compile the code and run it.
A jar can be created by running `gradle fatJar` and the jar file can be found in `build/libs/`.

### Building the Docker image
The application can be built and run using the Dockerfile. The watch directory containing the WARC files should be mounted so that the application can 
access thee files. The application is configured by using environment variables, these are LOCKSS_SOLR_WATCHDIR, LOCKSS_SOLR_URL and LOCKSS_SOLR_BATCH_SIZE.
Default values are provided by the application but these can be overridden when necessary. An example Docker command to start the application is given below.
```shell
docker run -it --rm -e LOCKSS_SOLR_WATCHDIR=/samples -e LOCKSS_SOLR_URL=http://192.168.56.103:8983/solr/test-core -v /home/rwincewicz/workspace/lockss/lockss-solr/samples:/samples:ro lockss/indexer
```
### Alternative Docker build (WP 20/10/2016):
Alternativily you can use the image from the hub, the following command will start a container with solr and create a tets-core:
```shell
docker run --name solr -d -p 8983:8983 solr solr-create -c test-core
```
Then run the application in Docker with as such:
```shell
docker run -it --rm --link solr:solr -e LOCKSS_SOLR_WATCHDIR=/samples -e LOCKSS_SOLR_URL=http://solr:8983/solr/test-core -v $WORKSPACE/lockss-solr/samples:/samples:ro lockss/indexer
```

### Docker Compose
It's also posible to use Docker Compose to build and start both containers. You'll need to [install Docker Compose](https://docs.docker.com/compose/install/), and run the following command:
```shell
docker-compose up --build
```
If you want to use a different WARCs folder than the default (i.e. `./samples`), the can be defined in `.env` as **LOCKSS_SOLR_WATCHDIR**
```
LOCKSS_SOLR_WATCHDIR=/var/data/warc
```

### Vagrant
A Vagrantfile has been added to run the app on a VM.

If you are using a different WARCs folder than `./samples`, you'll have to make sure it's shared by updating the `Vagrantfile`. 
```
config.vm.synced_folder "/var/data/warc", "/var/data/warc"
```

You need to [install Vagrant]:(https://www.vagrantup.com/docs/installation/) and run the following command:
```shell
vagrant up
```
This should start a VM running CentOS 7 with `Docker` and `Docker Compose` and other software. Please read the original Vagrant box page for details: [Docker-enabled Vagrant boxes](https://github.com/William-Yeh/docker-enabled-vagrant).

The VM is also running cAdvisor which can be access at [http://localhost:58080/containers/](http://localhost:58080/containers/)