# Dockerfile for jSS7 MAP Load Test (MO SMS)
# Build context: jss7/map/load directory

FROM eclipse-temurin:17-jdk-alpine

# Install Ant
RUN apk add --no-cache ant

# Set working directory
WORKDIR /opt/jss7-load-test

# Create necessary directories
RUN mkdir -p server client target/load

# Copy ant build file
COPY mo_sms_build.xml .

# Copy all required JARs from target/load
# These should be built beforehand using: mvn install -Passemble
COPY target/load/*.jar target/load/

# Copy resources if any
COPY src/main/resources src/main/resources

# Copy default log4j config
COPY src/main/resources/log4j.properties .

# Environment variables for test configuration
ENV TEST_SERVER_CHANNEL_TYPE=sctp
ENV TEST_SERVER_HOST_IP=0.0.0.0
ENV TEST_SERVER_HOST_PORT=8011
ENV TEST_SERVER_EXTRA_HOST_ADDRESS=-1
ENV TEST_SERVER_PEER_IP=127.0.0.1
ENV TEST_SERVER_PEER_PORT=8012
ENV TEST_SERVER_AS_FUNCTIONALITY=IPSP
ENV TEST_SERVER_RC=101
ENV TEST_SERVER_NA=102
ENV TEST_SERVER_ORIGINATING_PC=1
ENV TEST_SERVER_DESTINATION_PC=2
ENV TEST_SERVER_SI=3
ENV TEST_SERVER_NI=2
ENV TEST_SERVER_SSN=8
ENV TEST_SERVER_REMOTE_SSN=8

ENV TEST_CLIENT_NUM_OF_DIALOGS=1440000
ENV TEST_CLIENT_CONCURRENT_DIALOG=400
ENV TEST_CLIENT_CHANNEL_TYPE=sctp
ENV TEST_CLIENT_HOST_IP=127.0.0.1
ENV TEST_CLIENT_HOST_PORT=8011
ENV TEST_CLIENT_EXTRA_HOST_ADDRESS=-1
ENV TEST_CLIENT_PEER_IP=127.0.0.1
ENV TEST_CLIENT_PEER_PORT=8012
ENV TEST_CLIENT_AS_FUNCTIONALITY=IPSP
ENV TEST_CLIENT_RC=101
ENV TEST_CLIENT_NA=102
ENV TEST_CLIENT_ORIGINATING_PC=1
ENV TEST_CLIENT_DESTINATION_PC=2
ENV TEST_CLIENT_SI=3
ENV TEST_CLIENT_NI=2
ENV TEST_CLIENT_SSN=8
ENV TEST_CLIENT_REMOTE_SSN=8
ENV TEST_CLIENT_CLIENT_ADDRESS=1111112
ENV TEST_CLIENT_SERVER_ADDRESS=9960639999

# JVM Options
ENV JVM_OPTS="-Xms2048m -Xmx2048m -Xmn128m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m -XX:+UseParallelGC -XX:+HeapDumpOnOutOfMemoryError"

# Expose potential ports
EXPOSE 8011 8012

# Entry point script
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["server"]
