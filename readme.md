# Dockerfile for Kafka

This project contains a Dockerfile that creates an image that does
not start an embedded Zookeeper. The image relies on a Zookeeper to
be present. This enables cluster deployments without hassle.

The Dockerfile uses the multi stage feature.

In addition, the image comes with a health check. This health check
is based on native JMX information, rather than sending messages to
some kind of test topic to keep it pure. For more details, see:
- https://github.com/Technolords/tool-kafka-healthcheck

## Example deployment

```bash
#!/bin/bash
echo "About to start Kafka-1 as docker container..."
docker run --detach \
        --name kafka-1 \
        --restart yes \
        --network="kafka-cluster" \
        --ip="192.168.100.100" \
        --volume /var/data/kafka1/config:/etc/kafka/config \
        --volume /var/data/kafka1/data:/etc/kafka/data \
        technolords/kafka:latest
echo "...done!"
```

## Example configuration

Using the volumes, one of the properties must be:

```properties
log.dirs=/etc/kafka/data
```

... also make sure the *zookeeper.connect* is defined.

For more properties, see:
- https://github.com/apache/kafka/blob/trunk/config/server.properties