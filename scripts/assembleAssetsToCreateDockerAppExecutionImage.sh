#!/usr/bin/env bash

mkdir build || true
mkdir build/docker || true
echo "copying artifact :pttg-financial-status-service-ui-build:/code/build/libs/${1} to build/docker/"
docker cp pttg-financial-status-service-ui-build:/code/build/libs/${1} build/docker/

cp src/main/docker/Dockerfile build/docker/
