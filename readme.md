# Dockerfile for Kafka

#### Index
- [Introduction](https://github.com/Technolords/docker-kafka#introduction)
- [Deployment example](https://github.com/Technolords/docker-kafka#example)
- [Image explained](https://github.com/Technolords/docker-kafka/wiki)

## Introduction

This project contains a Dockerfile that creates an image that does
not start an embedded Zookeeper. The image relies on a Zookeeper to
be present. This enables cluster deployments without hassle (the number
of ZooKeeper instances is unrelated to the number of Kafka
instances).

The image bootstraps the configuration files automatically (first run)
and additionally merges this with the provided environment variables.

## Example

The image expects three volumes to be defined, and any environment
variables meant for Kafka must be prefixed with `KAFKA.`. For example:

```bash
#!/bin/bash
echo "About to start Kafka-1 as docker container..."
docker run --detach \
    --name kafka-1 \
    --restart yes \
    --network="kafka-cluster" \
    --ip="192.168.100.100" \
    --volume /var/data/kafka1/config:/etc/kafka/install/config \
    --volume /var/data/kafka1/data:/etc/kafka/install/data \
    --volume /var/data/kafka1/logs:/etc/kafka/install/logs \
    --env "KAFKA.BROKER.ID=1" \
    --env "KAFKA.ZOOKEEPER.CONNECT=192.168.100.10:2181,192.168.100.11:2181,192.168.100.12:2181" \
    --env "KAFKA.WHAT.EVER=value" \
    technolords/kafka:latest
echo "...done!"
```

The *data* volume contains the `log.dirs` (i.e. topic data) while the *logs*
volume contains the Kafka logging itself (i.e. log4j).

Using the example above will create a `what.ever=value` entry in the
*server.properties* file.

For more properties, see:
- https://github.com/apache/kafka/blob/trunk/config/server.properties
