set version=%1
docker build    --build-arg BUILD_IMAGE=saauan/graalvm-maven:alpine ^
                --build-arg RUN_IMAGE=renanpalmeira/graalvm-alpine ^
                --build-arg JAVA_PATH="/usr/lib/jvm/graalvm-ce-1.0.0-rc5/bin/java" ^
                --build-arg VERSION=%version% ^
                -t saauan/ini:graalvm-v%version% .