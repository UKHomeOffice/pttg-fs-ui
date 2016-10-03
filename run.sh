#!/usr/bin/env bash
NAME=${NAME:-pttg-fs-ui}

JAR=$(find . -name ${NAME}*.jar|head -1)
java ${JAVA_OPTS}  -Dcom.sun.management.jmxremote.local.only=false -Djavax.net.ssl.trustStore=/data/truststore.jks -Djava.security.egd=file:/dev/./urandom -jar "${JAR}"
