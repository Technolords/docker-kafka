FROM openjdk:8

ARG POM_VERSION
ARG KAFKA_VERSION
ARG SCALA_VERSION=2.12

LABEL pom.version=${POM_VERSION}
LABEL scala.version=${SCALA_VERSION}
LABEL kafka.version=${KAFKA_VERSION}

RUN wget -q http://apache.mirrors.spacedump.net/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz -O /tmp/kafka.tgz && \
    mkdir -p /tmp/kafka && \
    tar xfz /tmp/kafka.tgz -C /tmp/kafka && \
    mkdir -p /etc/kafka && \
    mv /tmp/kafka/kafka_${SCALA_VERSION}-${KAFKA_VERSION} /etc/kafka/install && \
    rm /tmp/kafka.tgz

EXPOSE 9092

VOLUME ["/etc/kafka/config", "/etc/kafka/data"]

WORKDIR "/etc/kafka/install"

ENTRYPOINT ["./bin/kafka-server-start.sh", "/etc/kafka/config/server.properties"]
