FROM openjdk:8

ARG KAFKA_VERSION

LABEL kafka.version=${KAFKA_VERSION}

RUN mkdir -p /etc/kafka

# ADD target/${JAR_FILE} /etc/mock/mock.jar

ADD docker/run-kafka.sh /etc/kafka

RUN chmod +x /etc/kafka/run-kafka.sh

EXPOSE 9090

#HEALTHCHECK --interval=1m --timeout=10s \
#    CMD curl --fail http://localhost:9090/mock/cmd?config=current || exit 1

#VOLUME ["/etc/mock/config", "/var/mock/data"]

#WORKDIR "/etc/mock/"

ENTRYPOINT ["./run-kafka.sh"]