ARG BUILD_IMAGE
ARG RUN_IMAGE

# Check the version argument is set
FROM alpine
ARG VERSION
RUN test -n "${VERSION}"

# Build the target folder (the .jar)
FROM ${BUILD_IMAGE} as builder
COPY pom.xml /ini_soft/
COPY build.xml /ini_soft/
COPY src ini_soft/src/
WORKDIR /ini_soft
RUN mvn package -Dmaven.test.skip=true


# Run the .jar (INI)
FROM ${RUN_IMAGE}
ARG VERSION
LABEL   author="Saauan (tristan.coignion@gmail.com)"\
        version=${VERSION}
# Copy build files from the builder
COPY --from=builder /ini_soft/target /ini_soft/target
# Copy the configuration
COPY ./ini_config.json /ini_soft/
# Copy sample programs
COPY ./benchable_examples /ini_soft/benchable_examples
COPY ./ini ini_soft/ini
WORKDIR /ini_soft
ARG JAVA_PATH
# Entrypoint will execute INI
ENTRYPOINT [${JAVA_PATH}, "-Xss10M", "-classpath", "./target/ini.jar", "ini.Main"]
CMD ["--shell"]