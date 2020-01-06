#!/bin/sh

# This script assumes:
# - spark connector is at resource-managers/pbs/ inside spark project root.
# - spark project is already compiled with pbs support.
# - DOCKER_USERNAME, DOCKER_PASSWORD environment variables are set.

image="spark-with-pbspro"
default_tag="nightly"

# build image
# we go to the spark root to build image because we want the spark project files
# in the docker context while building.
cd ../../
sudo docker build -t $DOCKER_USERNAME/$image:$tag \
    -f resource-managers/pbs/Dockerfile .

# push to docker hub
sudo docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
sudo docker push $DOCKER_USERNAME/$image:$tag
