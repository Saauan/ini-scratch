set version=%1
docker build    --build-arg BUILD_IMAGE=maven ^
                --build-arg JAVA_PATH="/usr/bin/java" ^
                --build-arg VERSION=%version% ^
                -t saauan/ini-test:openjdk-v%version% ^
                -f test.Dockerfile .