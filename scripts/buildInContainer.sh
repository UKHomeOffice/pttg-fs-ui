#!/usr/bin/env bash

echo ${pwd}
node_modules/bower/bin/bower --allow-root install
# ./gradlew clean build
./gradlew -PversionOverride=0.1.0 clean build