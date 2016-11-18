
echo "## Working dir: /vagrant"
cd /vagrant

echo "## Run Docker Compose"
sudo /usr/local/bin/docker-compose up --build

#echo "## triger indexing"
#touch /vagrant/samples/*