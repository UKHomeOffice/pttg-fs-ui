#!/usr/bin/env bash
set -e
[ -n "${DEBUG}" ] && set -x

GRADLE_IMAGE="quay.io/ukhomeofficedigital/gradle-nodejs:v2.13.1"
GIT_COMMIT=${GIT_COMMIT:-$(git rev-parse --short HEAD)}
GIT_COMMIT=${GIT_COMMIT:0:7}
VERSION=$(grep ^version build.gradle | cut -d= -f 2 | tr -d ' ' | sed -e "s|'||g" | sed -e "s|version|v|g")

build_image_for_app_build() {
  docker build -t "${GRADLE_IMAGE}" -f Dockerfile.build .
}

build_app() {
  local MOUNT="${PWD}:/code"

  # Mount in the local gradle cache into the docker container
  [ -d "${HOME}/.gradle/caches" ] && MOUNT="${MOUNT} -v ${HOME}/.gradle/caches:/root/.gradle/caches"

  # Mount in local maven repository into the docker container
  [ -d "${HOME}/.m2/repository" ] && MOUNT="${MOUNT} -v ${HOME}/.m2/repository:/root/.m2/repository"

  # Mount in local gradle user directory
  [ -d "${HOME}/.gradle" ] && MOUNT="${MOUNT} -v ${HOME}/.gradle:/root/.gradle"

  ENV_OPTS="GIT_COMMIT=${GIT_COMMIT} -e VERSION=${VERSION}"
  [ -n "${BUILD_NUMBER}" ] && ENV_OPTS="BUILD_NUMBER=${BUILD_NUMBER} -e ${ENV_OPTS}"

  docker run -e ${ENV_OPTS} -v ${MOUNT} "${GRADLE_IMAGE}" "${@}"
}

set_props() {
  [ -n "${BUILD_NUMBER}" ] && VERSION="${VERSION}-${BUILD_NUMBER}"
  [ -n "${GIT_COMMIT}" ] && VERSION="$VERSION.${GIT_COMMIT}"
  echo "VERSION=${VERSION}" > version.properties
}

build_image_that_runs_app() {
  docker build --build-arg VERSION=${VERSION} --build-arg JAR_PATH=build/libs \
    -t quay.io/ukhomeofficedigital/pttg-fs-ui:${VERSION} .
}

publish() {
  docker push quay.io/ukhomeofficedigital/pttg-fs-ui:${VERSION}
}

docker_credentials() {
  local DOCKER_CREDS_DIR='/root/.docker'
  local SOURCE_CREDS='/root/.secrets/config.json'

  if [[ -n ${AWS_REGION} ]] && [[ -d /root/.aws ]] && [[ -n ${JENKINS_OPTS} ]] && [[ -n ${JENKINS_HOME_S3_BUCKET_NAME} ]]
  then
    echo "running ${FUNCNAME[0]}"
    echo "setting docker credentials"
    if [[ ! -d ${DOCKER_CREDS_DIR} ]];
    then
      mkdir ${DOCKER_CREDS_DIR}
      cp ${SOURCE_CREDS} ${DOCKER_CREDS_DIR}/config.json
    else
      cp ${SOURCE_CREDS} ${DOCKER_CREDS_DIR}/config.json
    fi
  else
    echo "no credentials setup needed on local laptop/workstation ..."
  fi
}

main() {
  echo "=== docker credentials setup"
  docker_credentials

  echo "=== build image that will be used to build the app"
  build_image_for_app_build

  echo "=== build the app"
  build_app "${@}"

  echo "=== set_props function"
  set_props

  echo "=== dockerBuild function"
  build_image_that_runs_app

  echo "=== dockerPublish function - temp disabled"
  publish
}

main "$@"
