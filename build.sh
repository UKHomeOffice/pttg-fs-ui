#!/usr/bin/env bash
set -e
[ -n "$DEBUG" ] && set -x

setup() {
	echo "build number:: " ${BUILD_NUMBER}

	VERSION_NUMBER=0.1.0
	ARTIFACT="pttg-financial-status-service-api-${VERSION_NUMBER}.jar"

	if [ -n "$BUILD_NUMBER" ]; then
		ARTIFACT="pttg-financial-status-service-api-${VERSION_NUMBER}.${BUILD_NUMBER}.jar"
	fi

	echo "Artifact being built: " ${ARTIFACT}

	if [ ! -x ./scripts/assembleAssetsToCreateDockerAppExecutionImage.sh ]; then
		echo "The project specific build file is missing or not executable."
		exit 1
	fi
}

cleanup() {
	CONTAINERS=$(docker ps -aq)

	if [ ! -z "$CONTAINERS" -a "$CONTAINERS" != " " ]; then
		docker stop $CONTAINERS
		docker rm $CONTAINERS
	fi

	IMAGES=$(docker images | grep pttg-financial-status-service-api-build | awk '{print $3'})

	if [ ! -z "$IMAGES" -a "$IMAGES" != " " ]; then
		docker rmi -f $IMAGES
	fi
}

createDockerImageToExecuteBuildIn() {
	echo "building docker image to execute app build"
	docker build -f src/main/docker/Dockerfile.build -t pttg-financial-status-service-api-build .
}

executeBuild() {
	echo "running docker image as named container - pttg-financial-status-service-api-build"
	docker run --name pttg-financial-status-service-api-build\
		-e "VERSION_NUMBER=${VERSION_NUMBER}" \
		-e "BUILD_NUMBER=${BUILD_NUMBER}" \
		pttg-financial-status-service-api-build
}

# Make this call an external script that populates the folder that is then copied into the Docker image.
assembleAssetsToCreateDockerAppExecutionImage () {

	# Call project specific script to build image
	./scripts/assembleAssetsToCreateDockerAppExecutionImage.sh ${ARTIFACT}

#	mkdir build || true
#	mkdir build/docker || true
#	docker cp build.uk.gov.digital.ho.proving.financial.service:/code/build/build/libs/$ARTIFACT build/docker/
#	cp src/main/resources/run.sh build/docker/
#	cp -R src/main/resources/json build/docker/
#	cp src/main/docker/Dockerfile build/docker/
}

buildDockerAppExecutionImage() {
	echo "build docker image for app::"
	cd build/docker
	#TAG="quay.io/ukhomeofficedigital/uk.gov.digital.ho.financial.income.api:${VERSION_NUMBER}.${BUILD_NUMBER}"
	if [[ -n ${BUILD_NUMBER} ]]; then
		TAG="quay.io/ukhomeofficedigital/pttg-financial-status-service-api:${VERSION_NUMBER}.${BUILD_NUMBER}"
	else
		TAG="quay.io/ukhomeofficedigital/pttg-financial-status-service-api:${VERSION_NUMBER}"
	fi
	echo "building " ${TAG}
	docker build -t ${TAG} .
}

pushImageToRepo() {
	docker push ${TAG}
}

# -----
# Build
# -----

# Inputs:
#   1. financial status service api
#   2. Version
#   3. Build number

setup
cleanup
createDockerImageToExecuteBuildIn
executeBuild
assembleAssetsToCreateDockerAppExecutionImage
cleanup
buildDockerAppExecutionImage
pushImageToRepo
cleanup
