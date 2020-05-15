ARG BUILD_IMAGE

# Check the version argument is set
FROM alpine
ARG VERSION
RUN test -n "${VERSION}"

# Build the target folder (the .jar)
FROM ${BUILD_IMAGE}
COPY pom.xml /ini_soft/
COPY build.xml /ini_soft/
COPY src ini_soft/src/
COPY spin /ini_soft/spin
COPY java /ini_soft/java
COPY ini /ini_soft/ini
COPY ini_config.json /ini_soft/
WORKDIR /ini_soft
RUN mvn package -Dmaven.test.skip=true
CMD ["sh"]
