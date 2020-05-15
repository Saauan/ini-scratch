version=$1
docker build    --build-arg BUILD_IMAGE=maven \
                --build-arg RUN_IMAGE=openjdk:8-alpine \
                --build-arg JAVA_PATH="/usr/bin/java" \
                --build-arg VERSION=$version \
                -t saauan/ini:openjdk-v$version .