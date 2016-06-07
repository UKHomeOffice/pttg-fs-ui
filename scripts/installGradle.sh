#!/usr/bin/env bash

# installs to /opt/gradle
# existing versions are not overwritten/deleted
# seamless upgrades/downgrades
# $GRADLE_HOME points to latest *installed* (not released)
gradle_version=2.5
wget -N https://services.gradle.org/distributions/gradle-${gradle_version}-all.zip
ls
unzip -foq gradle-${gradle_version}-all.zip -d /opt/gradle
ls /opt
ln -sfn gradle-${gradle_version} /opt/gradle/latest
printf "export GRADLE_HOME=/opt/gradle/latest\nexport PATH=\$PATH:\$GRADLE_HOME/bin" > /etc/profile.d/gradle.sh
. /etc/profile.d/gradle.sh
# check installation
gradle -v