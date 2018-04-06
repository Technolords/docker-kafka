ARG POM_VERSION
ARG KAFKA_VERSION
ARG SCALA_VERSION=2.12
ARG HEALTH_CHECK_VERSION
ARG DOCKER_RUN_VERSION

FROM alpine:latest AS health-check-fetcher

ARG HEALTH_CHECK_VERSION

RUN wget https://repo1.maven.org/maven2/net/technolords/tool/kafka/health-check/${HEALTH_CHECK_VERSION}/health-check-${HEALTH_CHECK_VERSION}.jar -O /tmp/health-check.jar

FROM alpine:latest AS kafka-fetcher

ARG KAFKA_VERSION
ARG SCALA_VERSION

RUN wget -q http://apache.mirrors.spacedump.net/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz -O /tmp/kafka.tgz && \
    mkdir -p /tmp/kafka && \
    tar xfz /tmp/kafka.tgz -C /tmp/kafka && \
    mv /tmp/kafka/kafka_${SCALA_VERSION}-${KAFKA_VERSION}/config /tmp/kafka/kafka_${SCALA_VERSION}-${KAFKA_VERSION}/config-ref && \
    rm -rf /tmp/kafka/kafka_${SCALA_VERSION}-${KAFKA_VERSION}/bin/windows && \
    rm -rf /tmp/kafka/kafka_${SCALA_VERSION}-${KAFKA_VERSION}/site-docs

FROM openjdk:8

ARG POM_VERSION
ARG KAFKA_VERSION
ARG SCALA_VERSION
ARG HEALTH_CHECK_VERSION
ARG DOCKER_RUN_VERSION

LABEL pom.version=${POM_VERSION}
LABEL scala.version=${SCALA_VERSION}
LABEL kafka.version=${KAFKA_VERSION}
LABEL health.check.version=${HEALTH_CHECK_VERSION}
LABEL docker.run.version=${DOCKER_RUN_VERSION}

COPY --from=kafka-fetcher /tmp/kafka/kafka_${SCALA_VERSION}-${KAFKA_VERSION} /etc/kafka/install

COPY --from=health-check-fetcher /tmp/health-check.jar /etc/kafka/install

ADD target/${DOCKER_RUN_VERSION} /etc/kafka/install/docker-run.jar

EXPOSE 9092

VOLUME ["/etc/kafka/config", "/etc/kafka/data"]

WORKDIR "/etc/kafka/install"

HEALTHCHECK --interval=1m --timeout=10s \
    CMD java -jar health-check.jar | grep -P "^3"

ENTRYPOINT ["./bin/kafka-server-start.sh", "/etc/kafka/config/server.properties"]
