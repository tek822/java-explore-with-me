#!/bin/bash
#
docker container rm stats-server stats-db ewm-server ewm-db
IMAGES=`docker image ls --all | grep stats | awk '{print $3}'`
docker image rm $IMAGES
IMAGES=`docker image ls --all | grep ewm | awk '{print $3}'`
docker image rm $IMAGES
